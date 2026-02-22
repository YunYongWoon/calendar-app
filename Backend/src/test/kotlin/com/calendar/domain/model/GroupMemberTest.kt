package com.calendar.domain.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class GroupMemberTest : DescribeSpec({

    val groupId = GroupId(1L)
    val memberId = MemberId(1L)

    describe("GroupMember 생성") {
        context("createOwner로 생성하면") {
            it("OWNER 역할로 생성된다") {
                val owner = GroupMember.createOwner(groupId, memberId)

                owner.id.shouldBeNull()
                owner.groupId shouldBe groupId
                owner.memberId shouldBe memberId
                owner.role shouldBe GroupRole.OWNER
                owner.displayName.shouldBeNull()
                owner.color.shouldBeNull()
                owner.joinedAt.shouldNotBeNull()
            }
        }

        context("createMember로 생성하면") {
            it("MEMBER 역할로 생성된다") {
                val member = GroupMember.createMember(groupId, memberId)

                member.role shouldBe GroupRole.MEMBER
            }
        }
    }

    describe("changeRole") {
        it("역할이 변경된다") {
            val member = GroupMember.createMember(groupId, memberId)
            val admin = member.changeRole(GroupRole.ADMIN)

            admin.role shouldBe GroupRole.ADMIN
            admin.groupId shouldBe groupId
            admin.memberId shouldBe memberId
        }
    }

    describe("updateProfile") {
        context("표시 이름과 색상을 변경하면") {
            it("프로필이 업데이트된다") {
                val member = GroupMember.createMember(groupId, memberId)
                val updated = member.updateProfile(
                    displayName = DisplayName("엄마"),
                    color = ColorHex("#FF0000"),
                )

                updated.displayName?.value shouldBe "엄마"
                updated.color?.value shouldBe "#FF0000"
            }
        }

        context("일부 필드만 변경하면") {
            it("변경하지 않은 필드는 유지된다") {
                val member = GroupMember(
                    groupId = groupId,
                    memberId = memberId,
                    role = GroupRole.MEMBER,
                    displayName = DisplayName("기존"),
                    color = ColorHex("#000000"),
                )
                val updated = member.updateProfile(color = ColorHex("#FFFFFF"))

                updated.displayName?.value shouldBe "기존"
                updated.color?.value shouldBe "#FFFFFF"
            }
        }

        context("clearDisplayName이 true이면") {
            it("표시 이름이 null로 초기화된다") {
                val member = GroupMember(
                    groupId = groupId,
                    memberId = memberId,
                    role = GroupRole.MEMBER,
                    displayName = DisplayName("기존"),
                    color = ColorHex("#000000"),
                )
                val updated = member.updateProfile(clearDisplayName = true)

                updated.displayName.shouldBeNull()
                updated.color?.value shouldBe "#000000"
            }
        }

        context("clearColor가 true이면") {
            it("색상이 null로 초기화된다") {
                val member = GroupMember(
                    groupId = groupId,
                    memberId = memberId,
                    role = GroupRole.MEMBER,
                    displayName = DisplayName("기존"),
                    color = ColorHex("#000000"),
                )
                val updated = member.updateProfile(clearColor = true)

                updated.displayName?.value shouldBe "기존"
                updated.color.shouldBeNull()
            }
        }
    }

    describe("GroupRole") {
        context("OWNER이면") {
            it("canManage()는 true이다") {
                GroupRole.OWNER.canManage() shouldBe true
            }
            it("isOwner()는 true이다") {
                GroupRole.OWNER.isOwner() shouldBe true
            }
        }

        context("ADMIN이면") {
            it("canManage()는 true이다") {
                GroupRole.ADMIN.canManage() shouldBe true
            }
            it("isOwner()는 false이다") {
                GroupRole.ADMIN.isOwner() shouldBe false
            }
        }

        context("MEMBER이면") {
            it("canManage()는 false이다") {
                GroupRole.MEMBER.canManage() shouldBe false
            }
            it("isOwner()는 false이다") {
                GroupRole.MEMBER.isOwner() shouldBe false
            }
        }
    }
})
