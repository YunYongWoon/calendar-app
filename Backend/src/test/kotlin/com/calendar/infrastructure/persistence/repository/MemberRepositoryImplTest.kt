package com.calendar.infrastructure.persistence.repository

import com.calendar.domain.model.Email
import com.calendar.domain.model.Member
import com.calendar.domain.model.Nickname
import com.calendar.domain.model.Password
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import com.calendar.support.TestcontainersConfig
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig::class, MemberRepositoryImpl::class)
@ActiveProfiles("test")
class MemberRepositoryImplTest(
    private val memberRepository: MemberRepositoryImpl,
) : DescribeSpec({

    extensions(SpringExtension)

    describe("save") {
        context("새 회원을 저장하면") {
            it("ID가 할당되어 반환된다") {
                val member = Member.create(
                    email = Email("save@example.com"),
                    password = Password("hashedPassword"),
                    nickname = Nickname("테스터"),
                )

                val saved = memberRepository.save(member)

                saved.id shouldNotBe null
                saved.email.value shouldBe "save@example.com"
                saved.nickname.value shouldBe "테스터"
            }
        }
    }

    describe("findById") {
        context("존재하는 ID로 조회하면") {
            it("회원을 반환한다") {
                val member = memberRepository.save(
                    Member.create(
                        email = Email("findbyid@example.com"),
                        password = Password("hashedPassword"),
                        nickname = Nickname("조회테스트"),
                    ),
                )

                val found = memberRepository.findById(member.id!!)

                found shouldNotBe null
                found!!.email.value shouldBe "findbyid@example.com"
            }
        }

        context("존재하지 않는 ID로 조회하면") {
            it("null을 반환한다") {
                val found = memberRepository.findById(com.calendar.domain.model.MemberId(99999L))

                found shouldBe null
            }
        }
    }

    describe("findByEmail") {
        context("존재하는 이메일로 조회하면") {
            it("회원을 반환한다") {
                memberRepository.save(
                    Member.create(
                        email = Email("findbyemail@example.com"),
                        password = Password("hashedPassword"),
                        nickname = Nickname("이메일테스트"),
                    ),
                )

                val found = memberRepository.findByEmail(Email("findbyemail@example.com"))

                found shouldNotBe null
                found!!.nickname.value shouldBe "이메일테스트"
            }
        }

        context("존재하지 않는 이메일로 조회하면") {
            it("null을 반환한다") {
                val found = memberRepository.findByEmail(Email("notexist@example.com"))

                found shouldBe null
            }
        }
    }

    describe("existsByEmail") {
        context("이메일이 존재하면") {
            it("true를 반환한다") {
                memberRepository.save(
                    Member.create(
                        email = Email("exists@example.com"),
                        password = Password("hashedPassword"),
                        nickname = Nickname("존재테스트"),
                    ),
                )

                memberRepository.existsByEmail(Email("exists@example.com")) shouldBe true
            }
        }

        context("이메일이 존재하지 않으면") {
            it("false를 반환한다") {
                memberRepository.existsByEmail(Email("notexists@example.com")) shouldBe false
            }
        }
    }
})
