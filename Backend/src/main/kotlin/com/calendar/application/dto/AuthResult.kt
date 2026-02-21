package com.calendar.application.dto

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
)
