package com.calendar.domain.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows

class GroupNameTest : DescribeSpec({

    describe("GroupName 생성") {
        context("유효한 이름이면") {
            it("정상적으로 생성된다") {
                val name = GroupName("우리 가족")
                name.value shouldBe "우리 가족"
            }
        }

        context("빈 문자열이면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    GroupName("")
                }
            }
        }

        context("공백만 있으면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    GroupName("   ")
                }
            }
        }

        context("100자 이하이면") {
            it("정상적으로 생성된다") {
                val name = GroupName("가".repeat(100))
                name.value.length shouldBe 100
            }
        }

        context("101자 이상이면") {
            it("예외가 발생한다") {
                assertThrows<IllegalArgumentException> {
                    GroupName("가".repeat(101))
                }
            }
        }
    }
})
