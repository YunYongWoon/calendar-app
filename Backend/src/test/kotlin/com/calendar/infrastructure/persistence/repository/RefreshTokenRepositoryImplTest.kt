package com.calendar.infrastructure.persistence.repository

import com.calendar.domain.model.Email
import com.calendar.domain.model.Member
import com.calendar.domain.model.MemberId
import com.calendar.domain.model.Nickname
import com.calendar.domain.model.Password
import com.calendar.domain.model.RefreshToken
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@Import(RefreshTokenRepositoryImpl::class, MemberRepositoryImpl::class)
@ActiveProfiles("test")
class RefreshTokenRepositoryImplTest(
    private val refreshTokenRepository: RefreshTokenRepositoryImpl,
    private val memberRepository: MemberRepositoryImpl,
) : DescribeSpec({

    extensions(SpringExtension)

    lateinit var savedMember: Member

    beforeEach {
        savedMember = memberRepository.save(
            Member.create(
                email = Email("token-test-${System.nanoTime()}@example.com"),
                password = Password("hashedPassword"),
                nickname = Nickname("토큰테스트"),
            ),
        )
    }

    describe("save") {
        context("리프레시 토큰을 저장하면") {
            it("ID가 할당되어 반환된다") {
                val token = RefreshToken.create(
                    memberId = savedMember.id!!,
                    token = "uuid-token-save-${System.nanoTime()}",
                    expiresAt = LocalDateTime.now().plusDays(7),
                )

                val saved = refreshTokenRepository.save(token)

                saved.id shouldNotBe null
                saved.memberId shouldBe savedMember.id
            }
        }
    }

    describe("findByToken") {
        context("존재하는 토큰으로 조회하면") {
            it("리프레시 토큰을 반환한다") {
                val tokenValue = "uuid-token-find-${System.nanoTime()}"
                refreshTokenRepository.save(
                    RefreshToken.create(
                        memberId = savedMember.id!!,
                        token = tokenValue,
                        expiresAt = LocalDateTime.now().plusDays(7),
                    ),
                )

                val found = refreshTokenRepository.findByToken(tokenValue)

                found shouldNotBe null
                found!!.token shouldBe tokenValue
            }
        }

        context("존재하지 않는 토큰으로 조회하면") {
            it("null을 반환한다") {
                val found = refreshTokenRepository.findByToken("non-existent-token")

                found shouldBe null
            }
        }
    }

    describe("deleteAllByMemberId") {
        context("특정 회원의 모든 토큰을 삭제하면") {
            it("해당 회원의 토큰이 모두 삭제된다") {
                val tokenValue1 = "uuid-token-del1-${System.nanoTime()}"
                val tokenValue2 = "uuid-token-del2-${System.nanoTime()}"
                refreshTokenRepository.save(
                    RefreshToken.create(
                        memberId = savedMember.id!!,
                        token = tokenValue1,
                        expiresAt = LocalDateTime.now().plusDays(7),
                    ),
                )
                refreshTokenRepository.save(
                    RefreshToken.create(
                        memberId = savedMember.id!!,
                        token = tokenValue2,
                        expiresAt = LocalDateTime.now().plusDays(7),
                    ),
                )

                refreshTokenRepository.deleteAllByMemberId(savedMember.id!!)

                refreshTokenRepository.findByToken(tokenValue1) shouldBe null
                refreshTokenRepository.findByToken(tokenValue2) shouldBe null
            }
        }
    }
})
