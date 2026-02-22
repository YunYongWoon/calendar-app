package com.calendar.application.dto

import com.calendar.domain.model.CalendarGroup
import com.calendar.domain.model.GroupMember
import java.time.LocalDateTime

data class GroupResult(
    val id: Long,
    val name: String,
    val type: String,
    val description: String?,
    val coverImageUrl: String?,
    val maxMembers: Int,
    val memberCount: Int,
) {
    companion object {
        fun from(group: CalendarGroup, memberCount: Int): GroupResult = GroupResult(
            id = group.id?.value ?: error("저장된 CalendarGroup에 ID가 없습니다."),
            name = group.name.value,
            type = group.type.name,
            description = group.description,
            coverImageUrl = group.coverImageUrl,
            maxMembers = group.maxMembers,
            memberCount = memberCount,
        )
    }
}

data class GroupMemberResult(
    val id: Long,
    val groupId: Long,
    val memberId: Long,
    val role: String,
    val displayName: String?,
    val color: String?,
) {
    companion object {
        fun from(groupMember: GroupMember): GroupMemberResult = GroupMemberResult(
            id = groupMember.id?.value ?: error("저장된 GroupMember에 ID가 없습니다."),
            groupId = groupMember.groupId.value,
            memberId = groupMember.memberId.value,
            role = groupMember.role.name,
            displayName = groupMember.displayName?.value,
            color = groupMember.color?.value,
        )
    }
}

data class InviteCodeResult(
    val inviteCode: String,
    val expiresAt: LocalDateTime,
)
