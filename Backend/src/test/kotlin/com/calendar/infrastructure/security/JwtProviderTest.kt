package com.calendar.infrastructure.security

import com.calendar.domain.exception.ExpiredTokenException
import com.calendar.domain.exception.InvalidTokenException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank

class JwtProviderTest : DescribeSpec({

    val properties = JwtProperties(
        secret = "test-calendar-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256",
        accessTokenExpiry = 900_000L,    // 15분
        refreshTokenExpiry = 604_800_000L, // 7일
    )
    val jwtProvider = JwtProvider(properties)

    describe("generateAccessToken") {
        context("유효한 memberId로 토큰을 생성하면") {
            it("비어있지 않은 토큰 문자열을 반환한다") {
                val token = jwtProvider.generateAccessToken(1L)
                token.shouldNotBeBlank()
            }
        }
    }

    describe("extractMemberId") {
        context("유효한 토큰에서 memberId를 추출하면") {
            it("생성 시 사용한 memberId와 동일하다") {
                val memberId = 42L
                val token = jwtProvider.generateAccessToken(memberId)

                jwtProvider.extractMemberId(token) shouldBe memberId
            }
        }

        context("변조된 토큰이면") {
            it("InvalidTokenException이 발생한다") {
                shouldThrow<InvalidTokenException> {
                    jwtProvider.extractMemberId("invalid.token.value")
                }
            }
        }

        context("만료된 토큰이면") {
            it("ExpiredTokenException이 발생한다") {
                val expiredProperties = JwtProperties(
                    secret = properties.secret,
                    accessTokenExpiry = 0L, // 즉시 만료
                    refreshTokenExpiry = 0L,
                )
                val expiredJwtProvider = JwtProvider(expiredProperties)
                val token = expiredJwtProvider.generateAccessToken(1L)

                Thread.sleep(10) // 만료 보장

                shouldThrow<ExpiredTokenException> {
                    expiredJwtProvider.extractMemberId(token)
                }
            }
        }
    }

    describe("validateToken") {
        context("유효한 토큰이면") {
            it("true를 반환한다") {
                val token = jwtProvider.generateAccessToken(1L)
                jwtProvider.validateToken(token) shouldBe true
            }
        }

        context("변조된 토큰이면") {
            it("false를 반환한다") {
                jwtProvider.validateToken("invalid.token") shouldBe false
            }
        }

        context("만료된 토큰이면") {
            it("false를 반환한다") {
                val expiredProperties = JwtProperties(
                    secret = properties.secret,
                    accessTokenExpiry = 0L,
                    refreshTokenExpiry = 0L,
                )
                val expiredJwtProvider = JwtProvider(expiredProperties)
                val token = expiredJwtProvider.generateAccessToken(1L)

                Thread.sleep(10)

                expiredJwtProvider.validateToken(token) shouldBe false
            }
        }
    }
})
