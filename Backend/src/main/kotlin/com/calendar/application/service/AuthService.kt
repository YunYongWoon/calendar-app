package com.calendar.application.service

import com.calendar.application.dto.AuthResult
import com.calendar.application.dto.LoginCommand
import com.calendar.application.dto.MemberResult
import com.calendar.application.dto.RefreshCommand
import com.calendar.application.dto.SignupCommand
import com.calendar.application.usecase.AuthUseCase
import com.calendar.domain.exception.DuplicateEmailException
import com.calendar.domain.exception.InvalidCredentialsException
import com.calendar.domain.exception.InvalidTokenException
import com.calendar.domain.model.Email
import com.calendar.domain.model.Member
import com.calendar.domain.model.Nickname
import com.calendar.domain.model.Password
import com.calendar.domain.model.RefreshToken
import com.calendar.domain.repository.MemberRepository
import com.calendar.domain.repository.RefreshTokenRepository
import com.calendar.infrastructure.security.JwtProperties
import com.calendar.infrastructure.security.JwtProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional
class AuthService(
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProvider: JwtProvider,
    private val jwtProperties: JwtProperties,
    private val passwordEncoder: PasswordEncoder,
) : AuthUseCase {

    override fun signup(command: SignupCommand): MemberResult {
        val email = Email(command.email)
        if (memberRepository.existsByEmail(email)) {
            throw DuplicateEmailException()
        }

        val encodedPassword = passwordEncoder.encode(command.password)
        val member = Member.create(
            email = email,
            password = Password(encodedPassword),
            nickname = Nickname(command.nickname),
        )

        val savedMember = memberRepository.save(member)
        return MemberResult.from(savedMember)
    }

    @Transactional(readOnly = true)
    override fun login(command: LoginCommand): AuthResult {
        val email = Email(command.email)
        val member = memberRepository.findByEmail(email)
            ?: throw InvalidCredentialsException()

        if (!member.isActive) {
            throw InvalidCredentialsException()
        }

        if (!passwordEncoder.matches(command.password, member.password.value)) {
            throw InvalidCredentialsException()
        }

        return generateTokens(member)
    }

    override fun refresh(command: RefreshCommand): AuthResult {
        val storedToken = refreshTokenRepository.findByToken(command.refreshToken)
            ?: throw InvalidTokenException()

        if (storedToken.isExpired()) {
            refreshTokenRepository.deleteByToken(command.refreshToken)
            throw InvalidTokenException()
        }

        val member = memberRepository.findById(storedToken.memberId)
            ?: throw InvalidTokenException()

        // Token Rotation: 기존 토큰 삭제 후 새 토큰 발급
        refreshTokenRepository.deleteByToken(command.refreshToken)

        return generateTokens(member)
    }

    override fun logout(refreshToken: String) {
        refreshTokenRepository.deleteByToken(refreshToken)
    }

    private fun generateTokens(member: Member): AuthResult {
        val accessToken = jwtProvider.generateAccessToken(member.id!!.value)
        val refreshTokenValue = UUID.randomUUID().toString()

        val refreshToken = RefreshToken.create(
            memberId = member.id,
            token = refreshTokenValue,
            expiresAt = LocalDateTime.now().plusSeconds(jwtProperties.refreshTokenExpiry / 1000),
        )
        refreshTokenRepository.save(refreshToken)

        return AuthResult(
            accessToken = accessToken,
            refreshToken = refreshTokenValue,
        )
    }
}
