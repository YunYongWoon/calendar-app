package com.calendar.domain.model

import java.time.LocalDateTime

data class GroupMember(
    val id: GroupMemberId? = null,
    val groupId: GroupId,
    val memberId: MemberId,
    val role: GroupRole,
    val displayName: DisplayName? = null,
    val color: ColorHex? = null,
    val joinedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun changeRole(newRole: GroupRole): GroupMember = copy(role = newRole)

    fun updateProfile(
        displayName: DisplayName? = null,
        color: ColorHex? = null,
    ): GroupMember = copy(
        displayName = displayName ?: this.displayName,
        color = color ?: this.color,
    )

    companion object {
        fun createOwner(groupId: GroupId, memberId: MemberId): GroupMember = GroupMember(
            groupId = groupId,
            memberId = memberId,
            role = GroupRole.OWNER,
        )

        fun createMember(groupId: GroupId, memberId: MemberId): GroupMember = GroupMember(
            groupId = groupId,
            memberId = memberId,
            role = GroupRole.MEMBER,
        )
    }
}
