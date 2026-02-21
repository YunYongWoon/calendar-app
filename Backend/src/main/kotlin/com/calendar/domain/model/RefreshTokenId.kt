package com.calendar.domain.model

@JvmInline
value class RefreshTokenId(val value: Long) {
    init {
        require(value > 0) { "RefreshTokenId는 양수여야 합니다." }
    }
}
