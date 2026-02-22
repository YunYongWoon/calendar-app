package com.calendar.application.service

import com.calendar.application.dto.CreateGroupCommand
import com.calendar.application.dto.JoinGroupCommand
import com.calendar.application.dto.UpdateGroupCommand
import com.calendar.application.dto.UpdateGroupMemberCommand
import com.calendar.domain.exception.AlreadyGroupMemberException
import com.calendar.domain.exception.CannotRemoveOwnerException
import com.calendar.domain.exception.GroupMemberNotFoundException
import com.calendar.domain.exception.InsufficientPermissionException
import com.calendar.domain.exception.InvalidInviteCodeException
import com.calendar.domain.exception.MaxGroupLimitExceededException
import com.calendar.domain.exception.MaxMemberLimitExceededException
import com.calendar.domain.exception.OwnerCannotLeaveException
import com.calendar.domain.model.CalendarGroup
import com.calendar.domain.model.GroupId
import com.calendar.domain.model.GroupMember
import com.calendar.domain.model.GroupMemberId
import com.calendar.domain.model.GroupName
import com.calendar.domain.model.GroupRole
import com.calendar.domain.model.GroupType
import com.calendar.domain.model.InviteCode
import com.calendar.domain.model.MemberId
import com.calendar.domain.repository.CalendarGroupRepository
import com.calendar.domain.repository.GroupMemberRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class GroupServiceTest : DescribeSpec({

    val calendarGroupRepository = mockk<CalendarGroupRepository>()
    val groupMemberRepository = mockk<GroupMemberRepository>()
    val groupService = GroupService(calendarGroupRepository, groupMemberRepository)

    val memberId = 1L
    val groupId = 1L
    val savedGroup = CalendarGroup(
        id = GroupId(groupId),
        name = GroupName("테스트 그룹"),
        type = GroupType.FAMILY,
        description = "테스트 설명",
    )
    val ownerMember = GroupMember(
        id = GroupMemberId(1L),
        groupId = GroupId(groupId),
        memberId = MemberId(memberId),
        role = GroupRole.OWNER,
    )
    val regularMember = GroupMember(
        id = GroupMemberId(2L),
        groupId = GroupId(groupId),
        memberId = MemberId(2L),
        role = GroupRole.MEMBER,
    )

    describe("createGroup") {
        context("그룹 수가 최대 미만이면") {
            it("그룹이 생성된다") {
                every { groupMemberRepository.countByMemberId(MemberId(memberId)) } returns 0
                every { calendarGroupRepository.save(any()) } returns savedGroup
                every { groupMemberRepository.save(any()) } returns ownerMember

                val result = groupService.createGroup(
                    memberId,
                    CreateGroupCommand(name = "테스트 그룹", type = "FAMILY", description = "테스트 설명"),
                )

                result.name shouldBe "테스트 그룹"
                result.type shouldBe "FAMILY"
                result.memberCount shouldBe 1
            }
        }

        context("이미 10개 그룹에 가입되어 있으면") {
            it("MaxGroupLimitExceededException이 발생한다") {
                every { groupMemberRepository.countByMemberId(MemberId(memberId)) } returns 10

                assertThrows<MaxGroupLimitExceededException> {
                    groupService.createGroup(
                        memberId,
                        CreateGroupCommand(name = "새 그룹", type = "FRIEND"),
                    )
                }
            }
        }

        context("지원하지 않는 그룹 유형이면") {
            it("IllegalArgumentException이 발생한다") {
                every { groupMemberRepository.countByMemberId(MemberId(memberId)) } returns 0

                assertThrows<IllegalArgumentException> {
                    groupService.createGroup(
                        memberId,
                        CreateGroupCommand(name = "새 그룹", type = "INVALID"),
                    )
                }
            }
        }
    }

    describe("getMyGroups") {
        context("그룹에 가입되어 있으면") {
            it("그룹 목록을 반환한다") {
                every { groupMemberRepository.findAllByMemberId(MemberId(memberId)) } returns listOf(ownerMember)
                every { calendarGroupRepository.findByIdIn(listOf(GroupId(groupId))) } returns listOf(savedGroup)
                every { groupMemberRepository.countByGroupIds(listOf(GroupId(groupId))) } returns mapOf(GroupId(groupId) to 3)

                val result = groupService.getMyGroups(memberId)

                result shouldHaveSize 1
                result[0].name shouldBe "테스트 그룹"
                result[0].memberCount shouldBe 3
            }
        }

        context("그룹에 가입되어 있지 않으면") {
            it("빈 목록을 반환한다") {
                every { groupMemberRepository.findAllByMemberId(MemberId(memberId)) } returns emptyList()

                val result = groupService.getMyGroups(memberId)

                result shouldHaveSize 0
            }
        }
    }

    describe("getGroup") {
        context("그룹 멤버이면") {
            it("그룹 상세를 반환한다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns ownerMember
                every { calendarGroupRepository.findById(GroupId(groupId)) } returns savedGroup
                every { groupMemberRepository.countByGroupId(GroupId(groupId)) } returns 5

                val result = groupService.getGroup(memberId, groupId)

                result.name shouldBe "테스트 그룹"
                result.memberCount shouldBe 5
            }
        }

        context("그룹 멤버가 아니면") {
            it("GroupMemberNotFoundException이 발생한다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns null

                assertThrows<GroupMemberNotFoundException> {
                    groupService.getGroup(memberId, groupId)
                }
            }
        }
    }

    describe("updateGroup") {
        context("OWNER가 수정하면") {
            it("그룹이 수정된다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns ownerMember
                every { calendarGroupRepository.findById(GroupId(groupId)) } returns savedGroup
                every { calendarGroupRepository.save(any()) } answers { firstArg() }
                every { groupMemberRepository.countByGroupId(GroupId(groupId)) } returns 3

                val result = groupService.updateGroup(
                    memberId, groupId,
                    UpdateGroupCommand(name = "수정된 이름"),
                )

                result.name shouldBe "수정된 이름"
            }
        }

        context("MEMBER가 수정하면") {
            it("InsufficientPermissionException이 발생한다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(2L)) } returns regularMember

                assertThrows<InsufficientPermissionException> {
                    groupService.updateGroup(
                        2L, groupId,
                        UpdateGroupCommand(name = "수정 시도"),
                    )
                }
            }
        }
    }

    describe("deleteGroup") {
        context("OWNER가 삭제하면") {
            it("그룹이 삭제된다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns ownerMember
                every { calendarGroupRepository.findById(GroupId(groupId)) } returns savedGroup
                justRun { groupMemberRepository.deleteAllByGroupId(GroupId(groupId)) }
                justRun { calendarGroupRepository.delete(savedGroup) }

                groupService.deleteGroup(memberId, groupId)

                verify { groupMemberRepository.deleteAllByGroupId(GroupId(groupId)) }
                verify { calendarGroupRepository.delete(savedGroup) }
            }
        }

        context("MEMBER가 삭제하면") {
            it("InsufficientPermissionException이 발생한다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(2L)) } returns regularMember

                assertThrows<InsufficientPermissionException> {
                    groupService.deleteGroup(2L, groupId)
                }
            }
        }
    }

    describe("generateInviteCode") {
        context("OWNER가 초대 코드를 생성하면") {
            it("초대 코드가 반환된다") {
                val groupWithCode = savedGroup.generateInviteCode()
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns ownerMember
                every { calendarGroupRepository.findById(GroupId(groupId)) } returns savedGroup
                every { calendarGroupRepository.save(any()) } returns groupWithCode

                val result = groupService.generateInviteCode(memberId, groupId)

                result.inviteCode.length shouldBe 6
            }
        }

        context("MEMBER가 초대 코드를 생성하면") {
            it("InsufficientPermissionException이 발생한다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(2L)) } returns regularMember

                assertThrows<InsufficientPermissionException> {
                    groupService.generateInviteCode(2L, groupId)
                }
            }
        }
    }

    describe("joinGroup") {
        val inviteCode = InviteCode("ABC123")
        val groupWithCode = savedGroup.copy(
            inviteCode = inviteCode,
            inviteCodeExpiresAt = LocalDateTime.now().plusHours(24),
        )
        val newMemberId = 3L

        context("유효한 초대 코드로 참여하면") {
            it("그룹에 가입된다") {
                every { groupMemberRepository.countByMemberId(MemberId(newMemberId)) } returns 0
                every { calendarGroupRepository.findByInviteCode(inviteCode) } returns groupWithCode
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(newMemberId)) } returns null
                every { groupMemberRepository.countByGroupId(GroupId(groupId)) } returns 1
                every { groupMemberRepository.save(any()) } answers {
                    firstArg<GroupMember>().copy(id = GroupMemberId(3L))
                }

                val result = groupService.joinGroup(newMemberId, JoinGroupCommand("ABC123"))

                result.role shouldBe "MEMBER"
                result.memberId shouldBe newMemberId
            }
        }

        context("유효하지 않은 초대 코드이면") {
            it("InvalidInviteCodeException이 발생한다") {
                val wrongCode = InviteCode("ZZZ999")
                every { groupMemberRepository.countByMemberId(MemberId(newMemberId)) } returns 0
                every { calendarGroupRepository.findByInviteCode(wrongCode) } returns null

                assertThrows<InvalidInviteCodeException> {
                    groupService.joinGroup(newMemberId, JoinGroupCommand("ZZZ999"))
                }
            }
        }

        context("이미 가입된 멤버이면") {
            it("AlreadyGroupMemberException이 발생한다") {
                every { groupMemberRepository.countByMemberId(MemberId(newMemberId)) } returns 0
                every { calendarGroupRepository.findByInviteCode(inviteCode) } returns groupWithCode
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(newMemberId)) } returns regularMember

                assertThrows<AlreadyGroupMemberException> {
                    groupService.joinGroup(newMemberId, JoinGroupCommand("ABC123"))
                }
            }
        }

        context("그룹 인원이 가득 찼으면") {
            it("MaxMemberLimitExceededException이 발생한다") {
                val fullGroup = groupWithCode.copy(maxMembers = 2)
                every { groupMemberRepository.countByMemberId(MemberId(newMemberId)) } returns 0
                every { calendarGroupRepository.findByInviteCode(inviteCode) } returns fullGroup
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(newMemberId)) } returns null
                every { groupMemberRepository.countByGroupId(GroupId(groupId)) } returns 2

                assertThrows<MaxMemberLimitExceededException> {
                    groupService.joinGroup(newMemberId, JoinGroupCommand("ABC123"))
                }
            }
        }

        context("그룹 가입 수가 최대이면") {
            it("MaxGroupLimitExceededException이 발생한다") {
                every { groupMemberRepository.countByMemberId(MemberId(newMemberId)) } returns 10

                assertThrows<MaxGroupLimitExceededException> {
                    groupService.joinGroup(newMemberId, JoinGroupCommand("ABC123"))
                }
            }
        }
    }

    describe("getGroupMembers") {
        context("그룹 멤버이면") {
            it("멤버 목록을 반환한다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns ownerMember
                every { groupMemberRepository.findAllByGroupId(GroupId(groupId)) } returns listOf(ownerMember, regularMember)

                val result = groupService.getGroupMembers(memberId, groupId)

                result shouldHaveSize 2
            }
        }
    }

    describe("updateGroupMember") {
        context("OWNER가 멤버 역할을 변경하면") {
            it("역할이 변경된다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns ownerMember
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(2L)) } returns regularMember
                every { groupMemberRepository.save(any()) } answers {
                    firstArg<GroupMember>().copy(id = GroupMemberId(2L))
                }

                val result = groupService.updateGroupMember(
                    memberId, groupId, 2L,
                    UpdateGroupMemberCommand(role = "ADMIN"),
                )

                result.role shouldBe "ADMIN"
            }
        }

        context("MEMBER가 역할을 변경하면") {
            it("InsufficientPermissionException이 발생한다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(2L)) } returns regularMember

                assertThrows<InsufficientPermissionException> {
                    groupService.updateGroupMember(
                        2L, groupId, memberId,
                        UpdateGroupMemberCommand(role = "ADMIN"),
                    )
                }
            }
        }

        context("OWNER의 역할을 변경하려 하면") {
            it("CannotRemoveOwnerException이 발생한다") {
                val anotherOwner = ownerMember.copy(id = GroupMemberId(10L), memberId = MemberId(10L))
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns ownerMember
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(10L)) } returns anotherOwner

                assertThrows<CannotRemoveOwnerException> {
                    groupService.updateGroupMember(
                        memberId, groupId, 10L,
                        UpdateGroupMemberCommand(role = "MEMBER"),
                    )
                }
            }
        }

        context("OWNER 역할을 부여하려 하면") {
            it("InsufficientPermissionException이 발생한다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns ownerMember
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(2L)) } returns regularMember

                assertThrows<InsufficientPermissionException> {
                    groupService.updateGroupMember(
                        memberId, groupId, 2L,
                        UpdateGroupMemberCommand(role = "OWNER"),
                    )
                }
            }
        }

        context("지원하지 않는 역할이면") {
            it("IllegalArgumentException이 발생한다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns ownerMember
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(2L)) } returns regularMember

                assertThrows<IllegalArgumentException> {
                    groupService.updateGroupMember(
                        memberId, groupId, 2L,
                        UpdateGroupMemberCommand(role = "INVALID"),
                    )
                }
            }
        }
    }

    describe("removeGroupMember") {
        context("OWNER가 멤버를 내보내면") {
            it("멤버가 삭제된다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns ownerMember
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(2L)) } returns regularMember
                justRun { groupMemberRepository.delete(regularMember) }

                groupService.removeGroupMember(memberId, groupId, 2L)

                verify { groupMemberRepository.delete(regularMember) }
            }
        }

        context("OWNER를 내보내려 하면") {
            it("CannotRemoveOwnerException이 발생한다") {
                val anotherOwner = ownerMember.copy(memberId = MemberId(3L))
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns ownerMember
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(3L)) } returns anotherOwner

                assertThrows<CannotRemoveOwnerException> {
                    groupService.removeGroupMember(memberId, groupId, 3L)
                }
            }
        }

        context("MEMBER가 내보내려 하면") {
            it("InsufficientPermissionException이 발생한다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(2L)) } returns regularMember

                assertThrows<InsufficientPermissionException> {
                    groupService.removeGroupMember(2L, groupId, memberId)
                }
            }
        }
    }

    describe("leaveGroup") {
        context("MEMBER가 그룹을 나가면") {
            it("그룹에서 삭제된다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(2L)) } returns regularMember
                justRun { groupMemberRepository.delete(regularMember) }

                groupService.leaveGroup(2L, groupId)

                verify { groupMemberRepository.delete(regularMember) }
            }
        }

        context("OWNER가 그룹을 나가려 하면") {
            it("OwnerCannotLeaveException이 발생한다") {
                every { groupMemberRepository.findByGroupIdAndMemberId(GroupId(groupId), MemberId(memberId)) } returns ownerMember

                assertThrows<OwnerCannotLeaveException> {
                    groupService.leaveGroup(memberId, groupId)
                }
            }
        }
    }
})
