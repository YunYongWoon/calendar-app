package com.calendar.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PasswordTest : DescribeSpec({

    describe("Password 생성") {
        context("해시된 비밀번호 값이면") {
            it("정상적으로 생성된다") {
                val hashed = "\$2a\$10\$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUVWXYZ012"
                val password = Password(hashed)
                password.value shouldBe hashed
            }
        }

        context("빈 문자열이면") {
            it("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Password("")
                }
            }
        }

        context("공백만 있으면") {
            it("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Password("   ")
                }
            }
        }
    }
})
