package com.calendar.domain.exception

class MemberNotFoundException(
    message: String = "회원을 찾을 수 없습니다.",
) : CalendarException(errorCode = "MEMBER_NOT_FOUND", message = message)

class DuplicateEmailException(
    message: String = "이미 사용 중인 이메일입니다.",
) : CalendarException(errorCode = "DUPLICATE_EMAIL", message = message)
