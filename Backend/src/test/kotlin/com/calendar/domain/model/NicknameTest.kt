package com.calendar.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class NicknameTest : DescribeSpec({

    describe("Nickname 생성") {
        context("2~50자 범위이면") {
            it("정상적으로 생성된다") {
                val nickname = Nickname("홍길동")
                nickname.value shouldBe "홍길동"
            }
        }

        context("2자 최소 길이") {
            it("정상적으로 생성된다") {
                val nickname = Nickname("ab")
                nickname.value shouldBe "ab"
            }
        }

        context("50자 최대 길이") {
            it("정상적으로 생성된다") {
                val name = "a".repeat(50)
                val nickname = Nickname(name)
                nickname.value shouldBe name
            }
        }

        context("빈 문자열이면") {
            it("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Nickname("")
                }
            }
        }

        context("1자이면") {
            it("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Nickname("a")
                }
            }
        }

        context("51자 이상이면") {
            it("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Nickname("a".repeat(51))
                }
            }
        }
    }
})
