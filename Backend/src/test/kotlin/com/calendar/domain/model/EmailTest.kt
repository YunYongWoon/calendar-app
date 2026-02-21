package com.calendar.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class EmailTest : DescribeSpec({

    describe("Email 생성") {
        context("올바른 이메일 형식이면") {
            it("정상적으로 생성된다") {
                val email = Email("user@example.com")
                email.value shouldBe "user@example.com"
            }
        }

        context("다양한 유효한 이메일 형식") {
            listOf(
                "test@domain.com",
                "user.name@domain.com",
                "user+tag@domain.co.kr",
                "user123@sub.domain.com",
            ).forEach { validEmail ->
                it("$validEmail 은 정상 생성된다") {
                    Email(validEmail).value shouldBe validEmail
                }
            }
        }

        context("빈 문자열이면") {
            it("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Email("")
                }
            }
        }

        context("공백만 있으면") {
            it("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Email("   ")
                }
            }
        }

        context("@ 기호가 없으면") {
            it("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Email("invalid-email")
                }
            }
        }

        context("도메인이 없으면") {
            it("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Email("user@")
                }
            }
        }

        context("도메인 확장자가 없으면") {
            it("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Email("user@domain")
                }
            }
        }
    }
})
