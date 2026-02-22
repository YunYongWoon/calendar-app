package com.calendar.domain.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.assertThrows

class InviteCodeTest : DescribeSpec({

    describe("InviteCode 생성") {
        context("유효한 6자리 대문자 영숫자 코드이면") {
            it("정상적으로 생성된다") {
                val code = InviteCode("ABC123")
                code.value shouldBe "ABC123"
            }
        }

        context("소문자를 포함하면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    InviteCode("abc123")
                }
            }
        }

        context("빈 문자열이면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    InviteCode("")
                }
            }
        }

        context("5자리이면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    InviteCode("ABC12")
                }
            }
        }

        context("7자리이면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    InviteCode("ABC1234")
                }
            }
        }

        context("특수문자를 포함하면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    InviteCode("ABC12!")
                }
            }
        }
    }

    describe("of") {
        context("소문자를 포함한 코드이면") {
            it("대문자로 정규화하여 생성된다") {
                val code = InviteCode.of("abc123")
                code.value shouldBe "ABC123"
            }
        }

        context("대문자 코드이면") {
            it("그대로 생성된다") {
                val code = InviteCode.of("XYZ789")
                code.value shouldBe "XYZ789"
            }
        }
    }

    describe("generate") {
        it("6자리 대문자 영숫자 코드를 생성한다") {
            val code = InviteCode.generate()
            code.value shouldMatch "^[A-Z0-9]{6}$"
        }

        it("매번 다른 코드를 생성한다") {
            val codes = (1..10).map { InviteCode.generate().value }.toSet()
            (codes.size > 1) shouldBe true
        }
    }
})
