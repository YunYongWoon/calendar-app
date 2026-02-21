package com.calendar.domain.model

@JvmInline
value class Password(val value: String) {
    init {
        require(value.isNotBlank()) { "비밀번호는 비어있을 수 없습니다." }
    }
}
