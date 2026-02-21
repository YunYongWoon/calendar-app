package com.calendar.domain.model

import java.time.LocalDateTime

data class Member(
    val id: MemberId? = null,
    val email: Email,
    val password: Password,
    val nickname: Nickname,
    val profileImageUrl: String? = null,
    val status: MemberStatus = MemberStatus.ACTIVE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun updateProfile(
        nickname: Nickname? = null,
        profileImageUrl: String? = null,
    ): Member = copy(
        nickname = nickname ?: this.nickname,
        profileImageUrl = profileImageUrl ?: this.profileImageUrl,
        updatedAt = LocalDateTime.now(),
    )

    fun withdraw(): Member = copy(
        status = MemberStatus.DELETED,
        updatedAt = LocalDateTime.now(),
    )

    val isActive: Boolean get() = status == MemberStatus.ACTIVE

    companion object {
        fun create(
            email: Email,
            password: Password,
            nickname: Nickname,
        ): Member = Member(
            email = email,
            password = password,
            nickname = nickname,
        )
    }
}
