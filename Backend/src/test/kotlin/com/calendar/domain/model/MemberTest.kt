package com.calendar.domain.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MemberTest : DescribeSpec({

    describe("Member 생성") {
        context("create 팩토리 메서드로 생성하면") {
            it("ACTIVE 상태로 생성된다") {
                val member = Member.create(
                    email = Email("test@example.com"),
                    password = Password("hashedPassword"),
                    nickname = Nickname("테스터"),
                )

                member.email.value shouldBe "test@example.com"
                member.nickname.value shouldBe "테스터"
                member.status shouldBe MemberStatus.ACTIVE
                member.isActive shouldBe true
                member.id shouldBe null
                member.profileImageUrl shouldBe null
            }
        }
    }

    describe("프로필 수정") {
        val member = Member.create(
            email = Email("test@example.com"),
            password = Password("hashedPassword"),
            nickname = Nickname("원래닉네임"),
        )

        context("닉네임을 변경하면") {
            it("닉네임이 변경되고 updatedAt이 갱신된다") {
                val updated = member.updateProfile(nickname = Nickname("새닉네임"))

                updated.nickname.value shouldBe "새닉네임"
                updated.email shouldBe member.email
                updated.updatedAt shouldNotBe member.updatedAt
            }
        }

        context("프로필 이미지를 변경하면") {
            it("이미지 URL이 변경된다") {
                val updated = member.updateProfile(profileImageUrl = "https://img.example.com/new.jpg")

                updated.profileImageUrl shouldBe "https://img.example.com/new.jpg"
            }
        }

        context("아무것도 변경하지 않으면") {
            it("기존 값이 유지된다") {
                val updated = member.updateProfile()

                updated.nickname shouldBe member.nickname
                updated.profileImageUrl shouldBe member.profileImageUrl
            }
        }
    }

    describe("회원 탈퇴") {
        context("withdraw를 호출하면") {
            it("DELETED 상태로 변경된다") {
                val member = Member.create(
                    email = Email("test@example.com"),
                    password = Password("hashedPassword"),
                    nickname = Nickname("테스터"),
                )

                val withdrawn = member.withdraw()

                withdrawn.status shouldBe MemberStatus.DELETED
                withdrawn.isActive shouldBe false
            }
        }
    }
})
