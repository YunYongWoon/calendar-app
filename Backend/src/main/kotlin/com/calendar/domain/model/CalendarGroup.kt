package com.calendar.domain.model

import java.time.LocalDateTime

data class CalendarGroup(
    val id: GroupId? = null,
    val name: GroupName,
    val type: GroupType,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val inviteCode: InviteCode? = null,
    val inviteCodeExpiresAt: LocalDateTime? = null,
    val maxMembers: Int = DEFAULT_MAX_MEMBERS,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun update(
        name: GroupName? = null,
        description: String? = null,
        coverImageUrl: String? = null,
    ): CalendarGroup = copy(
        name = name ?: this.name,
        description = description ?: this.description,
        coverImageUrl = coverImageUrl ?: this.coverImageUrl,
        updatedAt = LocalDateTime.now(),
    )

    fun generateInviteCode(): CalendarGroup = copy(
        inviteCode = InviteCode.generate(),
        inviteCodeExpiresAt = LocalDateTime.now().plusHours(INVITE_CODE_VALID_HOURS),
        updatedAt = LocalDateTime.now(),
    )

    fun isInviteCodeValid(code: InviteCode, now: LocalDateTime = LocalDateTime.now()): Boolean =
        inviteCode != null &&
            inviteCode == code &&
            inviteCodeExpiresAt != null &&
            now.isBefore(inviteCodeExpiresAt)

    fun canAcceptNewMember(currentMemberCount: Int): Boolean =
        currentMemberCount < maxMembers

    companion object {
        const val DEFAULT_MAX_MEMBERS = 50
        const val INVITE_CODE_VALID_HOURS = 24L

        fun create(
            name: GroupName,
            type: GroupType,
            description: String? = null,
            coverImageUrl: String? = null,
        ): CalendarGroup = CalendarGroup(
            name = name,
            type = type,
            description = description,
            coverImageUrl = coverImageUrl,
        )
    }
}
