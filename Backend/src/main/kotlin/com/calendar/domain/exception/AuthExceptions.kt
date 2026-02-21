package com.calendar.domain.exception

class InvalidCredentialsException(
    message: String = "이메일 또는 비밀번호가 올바르지 않습니다.",
) : CalendarException(errorCode = "INVALID_CREDENTIALS", message = message)

class InvalidTokenException(
    message: String = "유효하지 않은 토큰입니다.",
) : CalendarException(errorCode = "INVALID_TOKEN", message = message)

class ExpiredTokenException(
    message: String = "만료된 토큰입니다.",
) : CalendarException(errorCode = "EXPIRED_TOKEN", message = message)
