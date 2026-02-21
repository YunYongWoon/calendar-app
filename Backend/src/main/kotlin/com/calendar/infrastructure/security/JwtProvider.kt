package com.calendar.infrastructure.security

import com.calendar.application.port.TokenProvider
import com.calendar.domain.exception.ExpiredTokenException
import com.calendar.domain.exception.InvalidTokenException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties,
) : TokenProvider {

    private val secretKey: SecretKey by lazy {
        val keyBytes = jwtProperties.secret.toByteArray()
        require(keyBytes.size >= 32) {
            "JWT 서명 키는 최소 256비트(32바이트)여야 합니다. 현재: ${keyBytes.size}바이트"
        }
        Keys.hmacShaKeyFor(keyBytes)
    }

    override fun generateAccessToken(memberId: Long): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpiry)

        return Jwts.builder()
            .subject(memberId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(secretKey)
            .compact()
    }

    override fun createRefreshTokenExpiry(): LocalDateTime =
        LocalDateTime.now().plusSeconds(jwtProperties.refreshTokenExpiry / 1000)

    fun extractMemberIdOrNull(token: String): Long? = try {
        parseClaimsOrThrow(token).subject.toLong()
    } catch (e: Exception) {
        null
    }

    private fun parseClaimsOrThrow(token: String) = try {
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    } catch (e: ExpiredJwtException) {
        throw ExpiredTokenException()
    } catch (e: JwtException) {
        throw InvalidTokenException()
    }
}
