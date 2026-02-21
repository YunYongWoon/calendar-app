package com.calendar.domain.exception

sealed class CalendarException(
    val errorCode: String,
    override val message: String,
) : RuntimeException(message)
