package com.calendar.domain.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class RefreshTokenTest : DescribeSpec({

    describe("RefreshToken 생성") {
        context("create 팩토리 메서드로 생성하면") {
            it("정상적으로 생성된다") {
                val token = RefreshToken.create(
                    memberId = MemberId(1L),
                    token = "some-uuid-token",
                    expiresAt = LocalDateTime.now().plusDays(7),
                )

                token.memberId.value shouldBe 1L
                token.token shouldBe "some-uuid-token"
                token.id shouldBe null
            }
        }
    }

    describe("만료 여부 확인") {
        context("만료 시간이 지나지 않았으면") {
            it("만료되지 않은 것으로 판단한다") {
                val token = RefreshToken.create(
                    memberId = MemberId(1L),
                    token = "valid-token",
                    expiresAt = LocalDateTime.now().plusDays(7),
                )

                token.isExpired() shouldBe false
            }
        }

        context("만료 시간이 지났으면") {
            it("만료된 것으로 판단한다") {
                val token = RefreshToken.create(
                    memberId = MemberId(1L),
                    token = "expired-token",
                    expiresAt = LocalDateTime.now().minusHours(1),
                )

                token.isExpired() shouldBe true
            }
        }

        context("특정 시각 기준으로 검사하면") {
            it("해당 시각 기준으로 판단한다") {
                val expiresAt = LocalDateTime.of(2026, 3, 1, 12, 0, 0)
                val token = RefreshToken.create(
                    memberId = MemberId(1L),
                    token = "test-token",
                    expiresAt = expiresAt,
                )

                token.isExpired(LocalDateTime.of(2026, 3, 1, 11, 0, 0)) shouldBe false
                token.isExpired(LocalDateTime.of(2026, 3, 1, 13, 0, 0)) shouldBe true
            }
        }
    }
})
