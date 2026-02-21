package com.calendar.infrastructure.persistence.repository

import com.calendar.domain.model.MemberId
import com.calendar.domain.model.RefreshToken
import com.calendar.domain.repository.RefreshTokenRepository
import com.calendar.infrastructure.persistence.entity.RefreshTokenEntity
import org.springframework.stereotype.Repository

@Repository
class RefreshTokenRepositoryImpl(
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) : RefreshTokenRepository {

    override fun save(refreshToken: RefreshToken): RefreshToken {
        val entity = RefreshTokenEntity.from(refreshToken)
        return refreshTokenJpaRepository.save(entity).toDomain()
    }

    override fun findByToken(token: String): RefreshToken? =
        refreshTokenJpaRepository.findByToken(token)?.toDomain()

    override fun deleteByToken(token: String) {
        refreshTokenJpaRepository.deleteByToken(token)
    }

    override fun deleteAllByMemberId(memberId: MemberId) {
        refreshTokenJpaRepository.deleteAllByMemberId(memberId.value)
    }
}
