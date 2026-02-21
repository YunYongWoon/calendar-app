package com.calendar.interfaces.rest.controller

import com.calendar.application.dto.MemberResult
import com.calendar.application.usecase.MemberUseCase
import com.calendar.domain.exception.MemberNotFoundException
import com.calendar.infrastructure.config.SecurityConfig
import com.calendar.infrastructure.security.CustomUserDetails
import com.calendar.infrastructure.security.JwtAuthenticationFilter
import com.calendar.infrastructure.security.JwtProvider
import com.calendar.interfaces.rest.handler.GlobalExceptionHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.justRun
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch

@WebMvcTest(MemberController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class, JwtAuthenticationFilter::class)
@ActiveProfiles("test")
class MemberControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean private val memberUseCase: MemberUseCase,
    @MockkBean private val jwtProvider: JwtProvider,
) : DescribeSpec({

    extensions(SpringExtension)

    fun mockAuth(): UsernamePasswordAuthenticationToken {
        val userDetails = CustomUserDetails(memberId = 1L)
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    describe("GET /members/me") {
        context("인증된 사용자가 요청하면") {
            it("200 OK와 회원 정보를 반환한다") {
                every { memberUseCase.getMe(1L) } returns MemberResult(
                    id = 1L,
                    email = "user@example.com",
                    nickname = "테스터",
                    profileImageUrl = null,
                    status = "ACTIVE",
                )

                mockMvc.get("/members/me") {
                    with(authentication(mockAuth()))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.email") { value("user@example.com") }
                    jsonPath("$.nickname") { value("테스터") }
                }
            }
        }

        context("인증되지 않은 사용자가 요청하면") {
            it("401 Unauthorized를 반환한다") {
                mockMvc.get("/members/me")
                    .andExpect {
                        status { isUnauthorized() }
                    }
            }
        }

        context("존재하지 않는 회원이면") {
            it("404 Not Found를 반환한다") {
                every { memberUseCase.getMe(1L) } throws MemberNotFoundException()

                mockMvc.get("/members/me") {
                    with(authentication(mockAuth()))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.errorCode") { value("MEMBER_NOT_FOUND") }
                }
            }
        }
    }

    describe("PATCH /members/me") {
        context("닉네임을 변경하면") {
            it("200 OK와 변경된 회원 정보를 반환한다") {
                every { memberUseCase.updateMe(1L, any()) } returns MemberResult(
                    id = 1L,
                    email = "user@example.com",
                    nickname = "새닉네임",
                    profileImageUrl = null,
                    status = "ACTIVE",
                )

                mockMvc.patch("/members/me") {
                    with(authentication(mockAuth()))
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("nickname" to "새닉네임"),
                    )
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.nickname") { value("새닉네임") }
                }
            }
        }
    }

    describe("DELETE /members/me") {
        context("인증된 사용자가 탈퇴하면") {
            it("204 No Content를 반환한다") {
                justRun { memberUseCase.deleteMe(1L) }

                mockMvc.delete("/members/me") {
                    with(authentication(mockAuth()))
                }.andExpect {
                    status { isNoContent() }
                }
            }
        }
    }
})
