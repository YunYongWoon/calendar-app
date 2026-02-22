package com.calendar.interfaces.rest.controller

import com.calendar.application.dto.GroupMemberResult
import com.calendar.application.dto.GroupResult
import com.calendar.application.dto.InviteCodeResult
import com.calendar.application.usecase.GroupUseCase
import com.calendar.domain.exception.GroupMemberNotFoundException
import com.calendar.domain.exception.GroupNotFoundException
import com.calendar.domain.exception.InsufficientPermissionException
import com.calendar.domain.exception.InvalidInviteCodeException
import com.calendar.domain.exception.OwnerCannotLeaveException
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
import org.springframework.test.web.servlet.post

@WebMvcTest(GroupController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class, JwtAuthenticationFilter::class)
@ActiveProfiles("test")
class GroupControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean private val groupUseCase: GroupUseCase,
    @MockkBean private val jwtProvider: JwtProvider,
) : DescribeSpec({

    extensions(SpringExtension)

    fun mockAuth(memberId: Long = 1L): UsernamePasswordAuthenticationToken {
        val userDetails = CustomUserDetails(memberId = memberId)
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    val groupResult = GroupResult(
        id = 1L,
        name = "테스트 그룹",
        type = "FAMILY",
        description = "테스트 설명",
        coverImageUrl = null,
        maxMembers = 50,
        memberCount = 1,
    )

    val groupMemberResult = GroupMemberResult(
        id = 1L,
        groupId = 1L,
        memberId = 1L,
        role = "OWNER",
        displayName = null,
        color = null,
    )

    describe("POST /groups") {
        context("유효한 요청이면") {
            it("201 Created와 그룹 정보를 반환한다") {
                every { groupUseCase.createGroup(1L, any()) } returns groupResult

                mockMvc.post("/groups") {
                    with(authentication(mockAuth()))
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("name" to "테스트 그룹", "type" to "FAMILY", "description" to "테스트 설명"),
                    )
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.name") { value("테스트 그룹") }
                    jsonPath("$.type") { value("FAMILY") }
                    jsonPath("$.memberCount") { value(1) }
                }
            }
        }

        context("이름이 빈 문자열이면") {
            it("400 Bad Request를 반환한다") {
                mockMvc.post("/groups") {
                    with(authentication(mockAuth()))
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("name" to "", "type" to "FAMILY"),
                    )
                }.andExpect {
                    status { isBadRequest() }
                    jsonPath("$.errorCode") { value("VALIDATION_ERROR") }
                }
            }
        }

        context("인증되지 않은 사용자이면") {
            it("401 Unauthorized를 반환한다") {
                mockMvc.post("/groups") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        mapOf("name" to "테스트", "type" to "FAMILY"),
                    )
                }.andExpect {
                    status { isUnauthorized() }
                }
            }
        }
    }

    describe("GET /groups") {
        context("인증된 사용자가 요청하면") {
            it("200 OK와 그룹 목록을 반환한다") {
                every { groupUseCase.getMyGroups(1L) } returns listOf(groupResult)

                mockMvc.get("/groups") {
                    with(authentication(mockAuth()))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$[0].name") { value("테스트 그룹") }
                }
            }
        }
    }

    describe("GET /groups/{id}") {
        context("그룹 멤버이면") {
            it("200 OK와 그룹 상세를 반환한다") {
                every { groupUseCase.getGroup(1L, 1L) } returns groupResult

                mockMvc.get("/groups/1") {
                    with(authentication(mockAuth()))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.name") { value("테스트 그룹") }
                }
            }
        }

        context("그룹이 존재하지 않으면") {
            it("404 Not Found를 반환한다") {
                every { groupUseCase.getGroup(1L, 999L) } throws GroupNotFoundException()

                mockMvc.get("/groups/999") {
                    with(authentication(mockAuth()))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.errorCode") { value("GROUP_NOT_FOUND") }
                }
            }
        }
    }

    describe("PATCH /groups/{id}") {
        context("OWNER가 수정하면") {
            it("200 OK와 수정된 그룹을 반환한다") {
                val updatedResult = groupResult.copy(name = "수정된 이름")
                every { groupUseCase.updateGroup(1L, 1L, any()) } returns updatedResult

                mockMvc.patch("/groups/1") {
                    with(authentication(mockAuth()))
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(mapOf("name" to "수정된 이름"))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.name") { value("수정된 이름") }
                }
            }
        }

        context("권한이 없으면") {
            it("403 Forbidden을 반환한다") {
                every { groupUseCase.updateGroup(1L, 1L, any()) } throws InsufficientPermissionException()

                mockMvc.patch("/groups/1") {
                    with(authentication(mockAuth()))
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(mapOf("name" to "수정 시도"))
                }.andExpect {
                    status { isForbidden() }
                    jsonPath("$.errorCode") { value("INSUFFICIENT_PERMISSION") }
                }
            }
        }
    }

    describe("DELETE /groups/{id}") {
        context("OWNER가 삭제하면") {
            it("204 No Content를 반환한다") {
                justRun { groupUseCase.deleteGroup(1L, 1L) }

                mockMvc.delete("/groups/1") {
                    with(authentication(mockAuth()))
                }.andExpect {
                    status { isNoContent() }
                }
            }
        }
    }

    describe("POST /groups/{id}/invite-code") {
        context("OWNER가 초대 코드를 생성하면") {
            it("200 OK와 초대 코드를 반환한다") {
                every { groupUseCase.generateInviteCode(1L, 1L) } returns InviteCodeResult(
                    inviteCode = "ABC123",
                    expiresAt = "2026-02-23T00:00:00",
                )

                mockMvc.post("/groups/1/invite-code") {
                    with(authentication(mockAuth()))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.inviteCode") { value("ABC123") }
                }
            }
        }
    }

    describe("POST /groups/join") {
        context("유효한 초대 코드로 참여하면") {
            it("201 Created와 멤버 정보를 반환한다") {
                val memberResult = GroupMemberResult(
                    id = 2L, groupId = 1L, memberId = 1L,
                    role = "MEMBER", displayName = null, color = null,
                )
                every { groupUseCase.joinGroup(1L, any()) } returns memberResult

                mockMvc.post("/groups/join") {
                    with(authentication(mockAuth()))
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(mapOf("inviteCode" to "ABC123"))
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.role") { value("MEMBER") }
                }
            }
        }

        context("잘못된 초대 코드 형식이면") {
            it("400 Bad Request를 반환한다") {
                mockMvc.post("/groups/join") {
                    with(authentication(mockAuth()))
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(mapOf("inviteCode" to "ABC"))
                }.andExpect {
                    status { isBadRequest() }
                }
            }
        }
    }

    describe("GET /groups/{id}/members") {
        context("그룹 멤버이면") {
            it("200 OK와 멤버 목록을 반환한다") {
                every { groupUseCase.getGroupMembers(1L, 1L) } returns listOf(groupMemberResult)

                mockMvc.get("/groups/1/members") {
                    with(authentication(mockAuth()))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$[0].role") { value("OWNER") }
                }
            }
        }
    }

    describe("PATCH /groups/{id}/members/{memberId}") {
        context("OWNER가 멤버 역할을 변경하면") {
            it("200 OK와 수정된 멤버 정보를 반환한다") {
                val updatedMember = groupMemberResult.copy(id = 2L, memberId = 2L, role = "ADMIN")
                every { groupUseCase.updateGroupMember(1L, 1L, 2L, any()) } returns updatedMember

                mockMvc.patch("/groups/1/members/2") {
                    with(authentication(mockAuth()))
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(mapOf("role" to "ADMIN"))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.role") { value("ADMIN") }
                }
            }
        }
    }

    describe("DELETE /groups/{id}/members/{memberId}") {
        context("OWNER가 멤버를 내보내면") {
            it("204 No Content를 반환한다") {
                justRun { groupUseCase.removeGroupMember(1L, 1L, 2L) }

                mockMvc.delete("/groups/1/members/2") {
                    with(authentication(mockAuth()))
                }.andExpect {
                    status { isNoContent() }
                }
            }
        }
    }

    describe("DELETE /groups/{id}/members/me") {
        context("MEMBER가 그룹을 나가면") {
            it("204 No Content를 반환한다") {
                justRun { groupUseCase.leaveGroup(1L, 1L) }

                mockMvc.delete("/groups/1/members/me") {
                    with(authentication(mockAuth()))
                }.andExpect {
                    status { isNoContent() }
                }
            }
        }

        context("OWNER가 그룹을 나가려 하면") {
            it("400 Bad Request를 반환한다") {
                every { groupUseCase.leaveGroup(1L, 1L) } throws OwnerCannotLeaveException()

                mockMvc.delete("/groups/1/members/me") {
                    with(authentication(mockAuth()))
                }.andExpect {
                    status { isBadRequest() }
                    jsonPath("$.errorCode") { value("OWNER_CANNOT_LEAVE") }
                }
            }
        }
    }
})
