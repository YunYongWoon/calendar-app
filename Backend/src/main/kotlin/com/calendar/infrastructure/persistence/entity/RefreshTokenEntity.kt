package com.calendar.infrastructure.persistence.entity

import com.calendar.domain.model.MemberId
import com.calendar.domain.model.RefreshToken
import com.calendar.domain.model.RefreshTokenId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_token")
class RefreshTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @Column(nullable = false, unique = true, length = 500)
    val token: String,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): RefreshToken = RefreshToken(
        id = RefreshTokenId(id),
        memberId = MemberId(memberId),
        token = token,
        expiresAt = expiresAt,
        createdAt = createdAt,
    )

    companion object {
        fun from(refreshToken: RefreshToken): RefreshTokenEntity = RefreshTokenEntity(
            id = refreshToken.id?.value ?: 0,
            memberId = refreshToken.memberId.value,
            token = refreshToken.token,
            expiresAt = refreshToken.expiresAt,
            createdAt = refreshToken.createdAt,
        )
    }
}
