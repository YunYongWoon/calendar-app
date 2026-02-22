package com.calendar.application.usecase

import com.calendar.application.dto.CreateGroupCommand
import com.calendar.application.dto.GroupMemberResult
import com.calendar.application.dto.GroupResult
import com.calendar.application.dto.InviteCodeResult
import com.calendar.application.dto.JoinGroupCommand
import com.calendar.application.dto.UpdateGroupCommand
import com.calendar.application.dto.UpdateGroupMemberCommand

interface GroupUseCase {
    fun createGroup(memberId: Long, command: CreateGroupCommand): GroupResult
    fun getMyGroups(memberId: Long): List<GroupResult>
    fun getGroup(memberId: Long, groupId: Long): GroupResult
    fun updateGroup(memberId: Long, groupId: Long, command: UpdateGroupCommand): GroupResult
    fun deleteGroup(memberId: Long, groupId: Long)
    fun generateInviteCode(memberId: Long, groupId: Long): InviteCodeResult
    fun joinGroup(memberId: Long, command: JoinGroupCommand): GroupMemberResult
    fun getGroupMembers(memberId: Long, groupId: Long): List<GroupMemberResult>
    fun updateGroupMember(memberId: Long, groupId: Long, targetMemberId: Long, command: UpdateGroupMemberCommand): GroupMemberResult
    fun removeGroupMember(memberId: Long, groupId: Long, targetMemberId: Long)
    fun leaveGroup(memberId: Long, groupId: Long)
}
