package com.calendar.interfaces.rest.dto

import java.time.LocalDateTime

data class ErrorResponse(
    val errorCode: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)
