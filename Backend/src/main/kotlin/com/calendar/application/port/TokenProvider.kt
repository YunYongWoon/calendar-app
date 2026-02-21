package com.calendar.application.port

import java.time.LocalDateTime

interface TokenProvider {
    fun generateAccessToken(memberId: Long): String
    fun createRefreshTokenExpiry(): LocalDateTime
}
