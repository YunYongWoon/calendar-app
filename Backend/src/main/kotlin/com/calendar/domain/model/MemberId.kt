package com.calendar.domain.model

@JvmInline
value class MemberId(val value: Long) {
    init {
        require(value > 0) { "MemberId는 양수여야 합니다." }
    }
}
