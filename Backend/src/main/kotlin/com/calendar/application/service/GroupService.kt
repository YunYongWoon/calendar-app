package com.calendar.application.service

import com.calendar.application.dto.CreateGroupCommand
import com.calendar.application.dto.GroupMemberResult
import com.calendar.application.dto.GroupResult
import com.calendar.application.dto.InviteCodeResult
import com.calendar.application.dto.JoinGroupCommand
import com.calendar.application.dto.UpdateGroupCommand
import com.calendar.application.dto.UpdateGroupMemberCommand
import com.calendar.application.usecase.GroupUseCase
import com.calendar.domain.exception.AlreadyGroupMemberException
import com.calendar.domain.exception.CannotRemoveOwnerException
import com.calendar.domain.exception.GroupMemberNotFoundException
import com.calendar.domain.exception.GroupNotFoundException
import com.calendar.domain.exception.InsufficientPermissionException
import com.calendar.domain.exception.InvalidInviteCodeException
import com.calendar.domain.exception.MaxGroupLimitExceededException
import com.calendar.domain.exception.MaxMemberLimitExceededException
import com.calendar.domain.exception.OwnerCannotLeaveException
import com.calendar.domain.model.CalendarGroup
import com.calendar.domain.model.ColorHex
import com.calendar.domain.model.DisplayName
import com.calendar.domain.model.GroupId
import com.calendar.domain.model.GroupMember
import com.calendar.domain.model.GroupName
import com.calendar.domain.model.GroupRole
import com.calendar.domain.model.GroupType
import com.calendar.domain.model.InviteCode
import com.calendar.domain.model.MemberId
import com.calendar.domain.repository.CalendarGroupRepository
import com.calendar.domain.repository.GroupMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GroupService(
    private val calendarGroupRepository: CalendarGroupRepository,
    private val groupMemberRepository: GroupMemberRepository,
) : GroupUseCase {

    @Transactional
    override fun createGroup(memberId: Long, command: CreateGroupCommand): GroupResult {
        val memberIdVo = MemberId(memberId)
        val groupCount = groupMemberRepository.countByMemberId(memberIdVo)
        if (groupCount >= MAX_GROUP_PER_MEMBER) {
            throw MaxGroupLimitExceededException()
        }

        val group = CalendarGroup.create(
            name = GroupName(command.name),
            type = GroupType.valueOf(command.type),
            description = command.description,
            coverImageUrl = command.coverImageUrl,
        )
        val savedGroup = calendarGroupRepository.save(group)

        val owner = GroupMember.createOwner(savedGroup.id!!, memberIdVo)
        groupMemberRepository.save(owner)

        return GroupResult.from(savedGroup, memberCount = 1)
    }

    override fun getMyGroups(memberId: Long): List<GroupResult> {
        val memberIdVo = MemberId(memberId)
        val groupMembers = groupMemberRepository.findAllByMemberId(memberIdVo)

        return groupMembers.mapNotNull { gm ->
            val group = calendarGroupRepository.findById(gm.groupId) ?: return@mapNotNull null
            val memberCount = groupMemberRepository.countByGroupId(gm.groupId)
            GroupResult.from(group, memberCount)
        }
    }

    override fun getGroup(memberId: Long, groupId: Long): GroupResult {
        val groupIdVo = GroupId(groupId)
        val memberIdVo = MemberId(memberId)

        requireGroupMember(groupIdVo, memberIdVo)
        val group = findGroupOrThrow(groupIdVo)
        val memberCount = groupMemberRepository.countByGroupId(groupIdVo)

        return GroupResult.from(group, memberCount)
    }

    @Transactional
    override fun updateGroup(memberId: Long, groupId: Long, command: UpdateGroupCommand): GroupResult {
        val groupIdVo = GroupId(groupId)
        val memberIdVo = MemberId(memberId)

        val groupMember = requireGroupMember(groupIdVo, memberIdVo)
        if (!groupMember.role.canManage()) {
            throw InsufficientPermissionException()
        }

        val group = findGroupOrThrow(groupIdVo)
        val updated = group.update(
            name = command.name?.let { GroupName(it) },
            description = command.description,
            coverImageUrl = command.coverImageUrl,
        )
        val saved = calendarGroupRepository.save(updated)
        val memberCount = groupMemberRepository.countByGroupId(groupIdVo)

        return GroupResult.from(saved, memberCount)
    }

    @Transactional
    override fun deleteGroup(memberId: Long, groupId: Long) {
        val groupIdVo = GroupId(groupId)
        val memberIdVo = MemberId(memberId)

        val groupMember = requireGroupMember(groupIdVo, memberIdVo)
        if (!groupMember.role.isOwner()) {
            throw InsufficientPermissionException()
        }

        val group = findGroupOrThrow(groupIdVo)
        groupMemberRepository.deleteAllByGroupId(groupIdVo)
        calendarGroupRepository.delete(group)
    }

    @Transactional
    override fun generateInviteCode(memberId: Long, groupId: Long): InviteCodeResult {
        val groupIdVo = GroupId(groupId)
        val memberIdVo = MemberId(memberId)

        val groupMember = requireGroupMember(groupIdVo, memberIdVo)
        if (!groupMember.role.canManage()) {
            throw InsufficientPermissionException()
        }

        val group = findGroupOrThrow(groupIdVo)
        val updated = group.generateInviteCode()
        val saved = calendarGroupRepository.save(updated)

        return InviteCodeResult(
            inviteCode = saved.inviteCode?.value ?: error("초대 코드 생성에 실패하였습니다."),
            expiresAt = saved.inviteCodeExpiresAt.toString(),
        )
    }

    @Transactional
    override fun joinGroup(memberId: Long, command: JoinGroupCommand): GroupMemberResult {
        val memberIdVo = MemberId(memberId)
        val inviteCode = InviteCode(command.inviteCode)

        val groupCount = groupMemberRepository.countByMemberId(memberIdVo)
        if (groupCount >= MAX_GROUP_PER_MEMBER) {
            throw MaxGroupLimitExceededException()
        }

        val group = calendarGroupRepository.findByInviteCode(inviteCode)
            ?: throw InvalidInviteCodeException()

        if (!group.isInviteCodeValid(inviteCode)) {
            throw InvalidInviteCodeException()
        }

        val existingMember = groupMemberRepository.findByGroupIdAndMemberId(group.id!!, memberIdVo)
        if (existingMember != null) {
            throw AlreadyGroupMemberException()
        }

        val currentMemberCount = groupMemberRepository.countByGroupId(group.id!!)
        if (!group.canAcceptNewMember(currentMemberCount)) {
            throw MaxMemberLimitExceededException()
        }

        val newMember = GroupMember.createMember(group.id!!, memberIdVo)
        val saved = groupMemberRepository.save(newMember)

        return GroupMemberResult.from(saved)
    }

    override fun getGroupMembers(memberId: Long, groupId: Long): List<GroupMemberResult> {
        val groupIdVo = GroupId(groupId)
        val memberIdVo = MemberId(memberId)

        requireGroupMember(groupIdVo, memberIdVo)

        return groupMemberRepository.findAllByGroupId(groupIdVo)
            .map { GroupMemberResult.from(it) }
    }

    @Transactional
    override fun updateGroupMember(
        memberId: Long,
        groupId: Long,
        targetMemberId: Long,
        command: UpdateGroupMemberCommand,
    ): GroupMemberResult {
        val groupIdVo = GroupId(groupId)
        val memberIdVo = MemberId(memberId)
        val targetMemberIdVo = MemberId(targetMemberId)

        val requester = requireGroupMember(groupIdVo, memberIdVo)
        if (!requester.role.isOwner()) {
            throw InsufficientPermissionException()
        }

        val target = groupMemberRepository.findByGroupIdAndMemberId(groupIdVo, targetMemberIdVo)
            ?: throw GroupMemberNotFoundException()

        var updated = target
        if (command.role != null) {
            updated = updated.changeRole(GroupRole.valueOf(command.role))
        }
        if (command.displayName != null || command.color != null) {
            updated = updated.updateProfile(
                displayName = command.displayName?.let { DisplayName(it) },
                color = command.color?.let { ColorHex(it) },
            )
        }

        val saved = groupMemberRepository.save(updated)
        return GroupMemberResult.from(saved)
    }

    @Transactional
    override fun removeGroupMember(memberId: Long, groupId: Long, targetMemberId: Long) {
        val groupIdVo = GroupId(groupId)
        val memberIdVo = MemberId(memberId)
        val targetMemberIdVo = MemberId(targetMemberId)

        val requester = requireGroupMember(groupIdVo, memberIdVo)
        if (!requester.role.canManage()) {
            throw InsufficientPermissionException()
        }

        val target = groupMemberRepository.findByGroupIdAndMemberId(groupIdVo, targetMemberIdVo)
            ?: throw GroupMemberNotFoundException()

        if (target.role.isOwner()) {
            throw CannotRemoveOwnerException()
        }

        groupMemberRepository.delete(target)
    }

    @Transactional
    override fun leaveGroup(memberId: Long, groupId: Long) {
        val groupIdVo = GroupId(groupId)
        val memberIdVo = MemberId(memberId)

        val groupMember = requireGroupMember(groupIdVo, memberIdVo)
        if (groupMember.role.isOwner()) {
            throw OwnerCannotLeaveException()
        }

        groupMemberRepository.delete(groupMember)
    }

    private fun findGroupOrThrow(groupId: GroupId): CalendarGroup =
        calendarGroupRepository.findById(groupId) ?: throw GroupNotFoundException()

    private fun requireGroupMember(groupId: GroupId, memberId: MemberId): GroupMember =
        groupMemberRepository.findByGroupIdAndMemberId(groupId, memberId)
            ?: throw GroupMemberNotFoundException()

    companion object {
        const val MAX_GROUP_PER_MEMBER = 10
    }
}
