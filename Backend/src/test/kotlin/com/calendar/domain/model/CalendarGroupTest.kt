package com.calendar.domain.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime

class CalendarGroupTest : DescribeSpec({

    describe("CalendarGroup 생성") {
        context("create 팩토리 메서드로 생성하면") {
            it("기본값으로 생성된다") {
                val group = CalendarGroup.create(
                    name = GroupName("우리 가족"),
                    type = GroupType.FAMILY,
                )

                group.id.shouldBeNull()
                group.name.value shouldBe "우리 가족"
                group.type shouldBe GroupType.FAMILY
                group.description.shouldBeNull()
                group.coverImageUrl.shouldBeNull()
                group.inviteCode.shouldBeNull()
                group.inviteCodeExpiresAt.shouldBeNull()
                group.maxMembers shouldBe CalendarGroup.DEFAULT_MAX_MEMBERS
            }
        }

        context("설명과 커버 이미지를 포함하면") {
            it("모든 필드가 설정된다") {
                val group = CalendarGroup.create(
                    name = GroupName("친구들"),
                    type = GroupType.FRIEND,
                    description = "고등학교 친구들",
                    coverImageUrl = "https://example.com/cover.jpg",
                )

                group.description shouldBe "고등학교 친구들"
                group.coverImageUrl shouldBe "https://example.com/cover.jpg"
            }
        }
    }

    describe("update") {
        val group = CalendarGroup.create(
            name = GroupName("원래 이름"),
            type = GroupType.CUSTOM,
        )

        context("이름을 변경하면") {
            it("이름이 업데이트된다") {
                val updated = group.update(name = GroupName("새 이름"))
                updated.name.value shouldBe "새 이름"
                updated.type shouldBe GroupType.CUSTOM
            }
        }

        context("설명을 변경하면") {
            it("설명이 업데이트된다") {
                val updated = group.update(description = "새 설명")
                updated.description shouldBe "새 설명"
            }
        }
    }

    describe("generateInviteCode") {
        it("초대 코드와 만료 시간이 설정된다") {
            val group = CalendarGroup.create(
                name = GroupName("테스트"),
                type = GroupType.FRIEND,
            )

            val withCode = group.generateInviteCode()

            withCode.inviteCode.shouldNotBeNull()
            withCode.inviteCodeExpiresAt.shouldNotBeNull()
        }
    }

    describe("isInviteCodeValid") {
        context("유효한 초대 코드이면") {
            it("true를 반환한다") {
                val code = InviteCode("ABC123")
                val group = CalendarGroup(
                    name = GroupName("테스트"),
                    type = GroupType.FRIEND,
                    inviteCode = code,
                    inviteCodeExpiresAt = LocalDateTime.now().plusHours(1),
                )

                group.isInviteCodeValid(code) shouldBe true
            }
        }

        context("초대 코드가 다르면") {
            it("false를 반환한다") {
                val group = CalendarGroup(
                    name = GroupName("테스트"),
                    type = GroupType.FRIEND,
                    inviteCode = InviteCode("ABC123"),
                    inviteCodeExpiresAt = LocalDateTime.now().plusHours(1),
                )

                group.isInviteCodeValid(InviteCode("XYZ789")) shouldBe false
            }
        }

        context("만료된 초대 코드이면") {
            it("false를 반환한다") {
                val code = InviteCode("ABC123")
                val group = CalendarGroup(
                    name = GroupName("테스트"),
                    type = GroupType.FRIEND,
                    inviteCode = code,
                    inviteCodeExpiresAt = LocalDateTime.now().minusHours(1),
                )

                group.isInviteCodeValid(code) shouldBe false
            }
        }

        context("초대 코드가 없으면") {
            it("false를 반환한다") {
                val group = CalendarGroup.create(
                    name = GroupName("테스트"),
                    type = GroupType.FRIEND,
                )

                group.isInviteCodeValid(InviteCode("ABC123")) shouldBe false
            }
        }
    }

    describe("canAcceptNewMember") {
        context("현재 인원이 최대 인원 미만이면") {
            it("true를 반환한다") {
                val group = CalendarGroup(
                    name = GroupName("테스트"),
                    type = GroupType.FRIEND,
                    maxMembers = 10,
                )

                group.canAcceptNewMember(9) shouldBe true
            }
        }

        context("현재 인원이 최대 인원과 같으면") {
            it("false를 반환한다") {
                val group = CalendarGroup(
                    name = GroupName("테스트"),
                    type = GroupType.FRIEND,
                    maxMembers = 10,
                )

                group.canAcceptNewMember(10) shouldBe false
            }
        }
    }
})
