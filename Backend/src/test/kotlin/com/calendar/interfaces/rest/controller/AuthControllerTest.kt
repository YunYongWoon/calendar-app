package com.calendar.interfaces.rest.controller

import com.calendar.application.dto.AuthResult
import com.calendar.application.dto.MemberResult
import com.calendar.application.usecase.AuthUseCase
import com.calendar.domain.exception.DuplicateEmailException
import com.calendar.domain.exception.InvalidCredentialsException
import com.calendar.infrastructure.config.SecurityConfig
import com.calendar.infrastructure.security.JwtAuthenticationFilter
import com.calendar.infrastructure.security.JwtProvider
import com.calendar.interfaces.rest.handler.GlobalExceptionHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(AuthController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class, JwtAuthenticationFilter::class)
@ActiveProfiles("test")
class AuthControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean private val authUseCase: AuthUseCase,
    @MockkBean private val jwtProvider: JwtProvider,
) : DescribeSpec({

    extensions(SpringExtension)

    describe("POST /auth/signup") {
        context("유효한 요청이면") {
            it("201 Created와 회원 정보를 반환한다") {
                every { authUseCase.signup(any()) } returns MemberResult(
                    id = 1L,
                    email = "new@example.com",
                    nickname = "새회원",
                    profileImageUrl = null,
                    status = "ACTIVE",
                )

                mockMvc.post("/auth/signup") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf(
                            "email" to "new@example.com",
                            "password" to "password123",
                            "nickname" to "새회원",
                        ),
                    )
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.email") { value("new@example.com") }
                    jsonPath("$.nickname") { value("새회원") }
                }
            }
        }

        context("이메일이 중복이면") {
            it("409 Conflict를 반환한다") {
                every { authUseCase.signup(any()) } throws DuplicateEmailException()

                mockMvc.post("/auth/signup") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf(
                            "email" to "dup@example.com",
                            "password" to "password123",
                            "nickname" to "중복이메일",
                        ),
                    )
                }.andExpect {
                    status { isConflict() }
                    jsonPath("$.errorCode") { value("DUPLICATE_EMAIL") }
                }
            }
        }

        context("이메일이 비어있으면") {
            it("400 Bad Request를 반환한다") {
                mockMvc.post("/auth/signup") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf(
                            "email" to "",
                            "password" to "password123",
                            "nickname" to "테스터",
                        ),
                    )
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }

    describe("POST /auth/login") {
        context("올바른 자격 증명이면") {
            it("200 OK와 토큰을 반환한다") {
                every { authUseCase.login(any()) } returns AuthResult(
                    accessToken = "access-token",
                    refreshToken = "refresh-token",
                )

                mockMvc.post("/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf(
                            "email" to "user@example.com",
                            "password" to "password123",
                        ),
                    )
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.accessToken") { value("access-token") }
                    jsonPath("$.refreshToken") { value("refresh-token") }
                }
            }
        }

        context("잘못된 자격 증명이면") {
            it("401 Unauthorized를 반환한다") {
                every { authUseCase.login(any()) } throws InvalidCredentialsException()

                mockMvc.post("/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf(
                            "email" to "user@example.com",
                            "password" to "wrongpassword",
                        ),
                    )
                }.andExpect {
                    status { isUnauthorized() }
                    jsonPath("$.errorCode") { value("INVALID_CREDENTIALS") }
                }
            }
        }
    }

    describe("POST /auth/refresh") {
        context("유효한 리프레시 토큰이면") {
            it("200 OK와 새 토큰을 반환한다") {
                every { authUseCase.refresh(any()) } returns AuthResult(
                    accessToken = "new-access-token",
                    refreshToken = "new-refresh-token",
                )

                mockMvc.post("/auth/refresh") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("refreshToken" to "valid-refresh-token"),
                    )
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.accessToken") { value("new-access-token") }
                    jsonPath("$.refreshToken") { value("new-refresh-token") }
                }
            }
        }
    }

    describe("POST /auth/logout") {
        context("리프레시 토큰으로 로그아웃하면") {
            it("204 No Content를 반환한다") {
                every { authUseCase.logout(any()) } returns Unit

                mockMvc.post("/auth/logout") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("refreshToken" to "some-refresh-token"),
                    )
                }.andExpect {
                    status { isNoContent() }
                }
            }
        }
    }
})
