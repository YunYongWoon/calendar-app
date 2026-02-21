package com.calendar.interfaces.rest.dto

import java.time.Instant

data class HealthResponse(
    val status: String,
    val timestamp: Instant,
    val version: String,
)

data class HealthDetailResponse(
    val status: String,
    val timestamp: Instant,
    val version: String,
    val javaVersion: String,
)
