package com.calendar.domain.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows

class DisplayNameTest : DescribeSpec({

    describe("DisplayName 생성") {
        context("유효한 표시 이름이면") {
            it("정상적으로 생성된다") {
                val displayName = DisplayName("엄마")
                displayName.value shouldBe "엄마"
            }
        }

        context("빈 문자열이면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    DisplayName("")
                }
            }
        }

        context("공백만 있으면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    DisplayName("   ")
                }
            }
        }

        context("50자 이하이면") {
            it("정상적으로 생성된다") {
                val displayName = DisplayName("가".repeat(50))
                displayName.value.length shouldBe 50
            }
        }

        context("51자 이상이면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    DisplayName("가".repeat(51))
                }
            }
        }
    }
})
