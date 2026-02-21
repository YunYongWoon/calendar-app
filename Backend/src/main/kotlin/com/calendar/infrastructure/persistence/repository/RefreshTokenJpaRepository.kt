package com.calendar.infrastructure.persistence.repository

import com.calendar.infrastructure.persistence.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun findByToken(token: String): RefreshTokenEntity?
    fun deleteByToken(token: String)
    fun deleteAllByMemberId(memberId: Long)
}
