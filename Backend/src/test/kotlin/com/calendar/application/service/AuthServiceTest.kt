package com.calendar.application.service

import com.calendar.application.dto.LoginCommand
import com.calendar.application.dto.RefreshCommand
import com.calendar.application.dto.SignupCommand
import com.calendar.domain.exception.DuplicateEmailException
import com.calendar.domain.exception.InvalidCredentialsException
import com.calendar.domain.exception.InvalidTokenException
import com.calendar.domain.model.Email
import com.calendar.domain.model.Member
import com.calendar.domain.model.MemberId
import com.calendar.domain.model.MemberStatus
import com.calendar.domain.model.Nickname
import com.calendar.domain.model.Password
import com.calendar.domain.model.RefreshToken
import com.calendar.domain.repository.MemberRepository
import com.calendar.domain.repository.RefreshTokenRepository
import com.calendar.infrastructure.security.JwtProperties
import com.calendar.infrastructure.security.JwtProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

class AuthServiceTest : DescribeSpec({

    val memberRepository = mockk<MemberRepository>()
    val refreshTokenRepository = mockk<RefreshTokenRepository>()
    val jwtProperties = JwtProperties(
        secret = "test-calendar-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256",
        accessTokenExpiry = 900_000L,
        refreshTokenExpiry = 604_800_000L,
    )
    val jwtProvider = JwtProvider(jwtProperties)
    val passwordEncoder = mockk<PasswordEncoder>()

    val authService = AuthService(
        memberRepository = memberRepository,
        refreshTokenRepository = refreshTokenRepository,
        jwtProvider = jwtProvider,
        jwtProperties = jwtProperties,
        passwordEncoder = passwordEncoder,
    )

    describe("signup") {
        context("새로운 이메일로 가입하면") {
            it("회원이 생성된다") {
                every { memberRepository.existsByEmail(Email("new@example.com")) } returns false
                every { passwordEncoder.encode("password123") } returns "encodedPassword"
                every { memberRepository.save(any()) } answers {
                    firstArg<Member>().copy(id = MemberId(1L))
                }

                val result = authService.signup(
                    SignupCommand("new@example.com", "password123", "새회원"),
                )

                result.email shouldBe "new@example.com"
                result.nickname shouldBe "새회원"
                result.id shouldBe 1L
            }
        }

        context("이미 존재하는 이메일이면") {
            it("DuplicateEmailException이 발생한다") {
                every { memberRepository.existsByEmail(Email("dup@example.com")) } returns true

                shouldThrow<DuplicateEmailException> {
                    authService.signup(
                        SignupCommand("dup@example.com", "password123", "중복이메일"),
                    )
                }
            }
        }
    }

    describe("login") {
        val existingMember = Member(
            id = MemberId(1L),
            email = Email("user@example.com"),
            password = Password("encodedPassword"),
            nickname = Nickname("기존회원"),
            status = MemberStatus.ACTIVE,
        )

        context("올바른 이메일과 비밀번호로 로그인하면") {
            it("액세스 토큰과 리프레시 토큰을 반환한다") {
                every { memberRepository.findByEmail(Email("user@example.com")) } returns existingMember
                every { passwordEncoder.matches("password123", "encodedPassword") } returns true
                every { refreshTokenRepository.save(any()) } answers { firstArg() }

                val result = authService.login(
                    LoginCommand("user@example.com", "password123"),
                )

                result.accessToken.shouldNotBeBlank()
                result.refreshToken.shouldNotBeBlank()
            }
        }

        context("존재하지 않는 이메일이면") {
            it("InvalidCredentialsException이 발생한다") {
                every { memberRepository.findByEmail(Email("notexist@example.com")) } returns null

                shouldThrow<InvalidCredentialsException> {
                    authService.login(
                        LoginCommand("notexist@example.com", "password123"),
                    )
                }
            }
        }

        context("비밀번호가 틀리면") {
            it("InvalidCredentialsException이 발생한다") {
                every { memberRepository.findByEmail(Email("user@example.com")) } returns existingMember
                every { passwordEncoder.matches("wrongpassword", "encodedPassword") } returns false

                shouldThrow<InvalidCredentialsException> {
                    authService.login(
                        LoginCommand("user@example.com", "wrongpassword"),
                    )
                }
            }
        }

        context("탈퇴한 회원이면") {
            it("InvalidCredentialsException이 발생한다") {
                val deletedMember = existingMember.copy(status = MemberStatus.DELETED)
                every { memberRepository.findByEmail(Email("user@example.com")) } returns deletedMember

                shouldThrow<InvalidCredentialsException> {
                    authService.login(
                        LoginCommand("user@example.com", "password123"),
                    )
                }
            }
        }
    }

    describe("refresh") {
        context("유효한 리프레시 토큰이면") {
            it("새 토큰 쌍을 반환한다") {
                val storedToken = RefreshToken(
                    id = com.calendar.domain.model.RefreshTokenId(1L),
                    memberId = MemberId(1L),
                    token = "valid-refresh-token",
                    expiresAt = LocalDateTime.now().plusDays(7),
                )
                val member = Member(
                    id = MemberId(1L),
                    email = Email("user@example.com"),
                    password = Password("encodedPassword"),
                    nickname = Nickname("기존회원"),
                )

                every { refreshTokenRepository.findByToken("valid-refresh-token") } returns storedToken
                justRun { refreshTokenRepository.deleteByToken("valid-refresh-token") }
                every { memberRepository.findById(MemberId(1L)) } returns member
                every { refreshTokenRepository.save(any()) } answers { firstArg() }

                val result = authService.refresh(RefreshCommand("valid-refresh-token"))

                result.accessToken.shouldNotBeBlank()
                result.refreshToken.shouldNotBeBlank()
            }
        }

        context("존재하지 않는 리프레시 토큰이면") {
            it("InvalidTokenException이 발생한다") {
                every { refreshTokenRepository.findByToken("invalid-token") } returns null

                shouldThrow<InvalidTokenException> {
                    authService.refresh(RefreshCommand("invalid-token"))
                }
            }
        }

        context("만료된 리프레시 토큰이면") {
            it("InvalidTokenException이 발생한다") {
                val expiredToken = RefreshToken(
                    id = com.calendar.domain.model.RefreshTokenId(1L),
                    memberId = MemberId(1L),
                    token = "expired-refresh-token",
                    expiresAt = LocalDateTime.now().minusHours(1),
                )

                every { refreshTokenRepository.findByToken("expired-refresh-token") } returns expiredToken
                justRun { refreshTokenRepository.deleteByToken("expired-refresh-token") }

                shouldThrow<InvalidTokenException> {
                    authService.refresh(RefreshCommand("expired-refresh-token"))
                }
            }
        }
    }

    describe("logout") {
        context("리프레시 토큰으로 로그아웃하면") {
            it("토큰이 삭제된다") {
                justRun { refreshTokenRepository.deleteByToken("some-token") }

                authService.logout("some-token")

                verify { refreshTokenRepository.deleteByToken("some-token") }
            }
        }
    }
})
