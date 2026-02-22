package com.calendar.domain.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows

class ColorHexTest : DescribeSpec({

    describe("ColorHex 생성") {
        context("유효한 #RRGGBB 형식이면") {
            it("정상적으로 생성된다") {
                val color = ColorHex("#FF0000")
                color.value shouldBe "#FF0000"
            }
        }

        context("소문자 hex이면") {
            it("정상적으로 생성된다") {
                val color = ColorHex("#ff00ff")
                color.value shouldBe "#ff00ff"
            }
        }

        context("#이 없으면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    ColorHex("FF0000")
                }
            }
        }

        context("6자리가 아니면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    ColorHex("#FFF")
                }
            }
        }

        context("유효하지 않은 hex 문자이면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    ColorHex("#GGGGGG")
                }
            }
        }

        context("빈 문자열이면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    ColorHex("")
                }
            }
        }
    }
})
