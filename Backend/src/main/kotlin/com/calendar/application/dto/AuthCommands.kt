package com.calendar.application.dto

data class SignupCommand(
    val email: String,
    val password: String,
    val nickname: String,
)

data class LoginCommand(
    val email: String,
    val password: String,
)

data class RefreshCommand(
    val refreshToken: String,
)
