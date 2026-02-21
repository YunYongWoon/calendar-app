package com.calendar.infrastructure.security

import com.calendar.domain.exception.ExpiredTokenException
import com.calendar.domain.exception.InvalidTokenException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties,
) {
    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    fun generateAccessToken(memberId: Long): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpiry)

        return Jwts.builder()
            .subject(memberId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(secretKey)
            .compact()
    }

    fun extractMemberId(token: String): Long {
        val claims = parseClaimsOrThrow(token)
        return claims.subject.toLong()
    }

    fun validateToken(token: String): Boolean {
        return try {
            parseClaimsOrThrow(token)
            true
        } catch (e: Exception) {
            false
        }
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
