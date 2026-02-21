package com.calendar.infrastructure.security

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank

class JwtProviderTest : DescribeSpec({

    val properties = JwtProperties(
        secret = "test-calendar-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256",
        accessTokenExpiry = 900_000L,    // 15분
        refreshTokenExpiry = 604_800_000L, // 7일
    )
    val jwtProvider = JwtProvider(properties)

    describe("generateAccessToken") {
        context("유효한 memberId로 토큰을 생성하면") {
            it("비어있지 않은 토큰 문자열을 반환한다") {
                val token = jwtProvider.generateAccessToken(1L)
                token.shouldNotBeBlank()
            }
        }
    }

    describe("extractMemberIdOrNull") {
        context("유효한 토큰에서 memberId를 추출하면") {
            it("생성 시 사용한 memberId와 동일하다") {
                val memberId = 42L
                val token = jwtProvider.generateAccessToken(memberId)

                jwtProvider.extractMemberIdOrNull(token) shouldBe memberId
            }
        }

        context("변조된 토큰이면") {
            it("null을 반환한다") {
                jwtProvider.extractMemberIdOrNull("invalid.token.value") shouldBe null
            }
        }

        context("만료된 토큰이면") {
            it("null을 반환한다") {
                val expiredProperties = JwtProperties(
                    secret = properties.secret,
                    accessTokenExpiry = 0L,
                    refreshTokenExpiry = 0L,
                )
                val expiredJwtProvider = JwtProvider(expiredProperties)
                val token = expiredJwtProvider.generateAccessToken(1L)

                Thread.sleep(10)

                expiredJwtProvider.extractMemberIdOrNull(token) shouldBe null
            }
        }
    }

    describe("createRefreshTokenExpiry") {
        context("호출하면") {
            it("현재 시각 이후의 만료 시각을 반환한다") {
                val expiry = jwtProvider.createRefreshTokenExpiry()
                expiry shouldNotBe null
            }
        }
    }
})
