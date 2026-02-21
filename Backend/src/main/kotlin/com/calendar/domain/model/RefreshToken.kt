package com.calendar.domain.model

import java.time.LocalDateTime

data class RefreshToken(
    val id: RefreshTokenId? = null,
    val memberId: MemberId,
    val token: String,
    val expiresAt: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun isExpired(now: LocalDateTime = LocalDateTime.now()): Boolean =
        now.isAfter(expiresAt)

    companion object {
        fun create(
            memberId: MemberId,
            token: String,
            expiresAt: LocalDateTime,
        ): RefreshToken = RefreshToken(
            memberId = memberId,
            token = token,
            expiresAt = expiresAt,
        )
    }
}
