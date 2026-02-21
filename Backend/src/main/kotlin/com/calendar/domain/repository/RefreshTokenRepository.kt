package com.calendar.domain.repository

import com.calendar.domain.model.MemberId
import com.calendar.domain.model.RefreshToken

interface RefreshTokenRepository {
    fun save(refreshToken: RefreshToken): RefreshToken
    fun findByToken(token: String): RefreshToken?
    fun deleteByToken(token: String)
    fun deleteAllByMemberId(memberId: MemberId)
}
