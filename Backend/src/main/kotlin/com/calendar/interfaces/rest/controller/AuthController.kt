package com.calendar.interfaces.rest.controller

import com.calendar.application.dto.LoginCommand
import com.calendar.application.dto.RefreshCommand
import com.calendar.application.dto.SignupCommand
import com.calendar.application.usecase.AuthUseCase
import com.calendar.interfaces.rest.dto.AuthResponse
import com.calendar.interfaces.rest.dto.LoginRequest
import com.calendar.interfaces.rest.dto.MemberResponse
import com.calendar.interfaces.rest.dto.RefreshRequest
import com.calendar.interfaces.rest.dto.SignupRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authUseCase: AuthUseCase,
) {

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: SignupRequest): ResponseEntity<MemberResponse> {
        val result = authUseCase.signup(
            SignupCommand(
                email = request.email,
                password = request.password,
                nickname = request.nickname,
            ),
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                MemberResponse(
                    id = result.id,
                    email = result.email,
                    nickname = result.nickname,
                    profileImageUrl = result.profileImageUrl,
                ),
            )
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val result = authUseCase.login(
            LoginCommand(
                email = request.email,
                password = request.password,
            ),
        )

        return ResponseEntity.ok(
            AuthResponse(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
            ),
        )
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshRequest): ResponseEntity<AuthResponse> {
        val result = authUseCase.refresh(
            RefreshCommand(refreshToken = request.refreshToken),
        )

        return ResponseEntity.ok(
            AuthResponse(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
            ),
        )
    }

    @PostMapping("/logout")
    fun logout(@Valid @RequestBody request: RefreshRequest): ResponseEntity<Void> {
        authUseCase.logout(request.refreshToken)
        return ResponseEntity.noContent().build()
    }
}
