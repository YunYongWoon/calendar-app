package com.calendar.domain.model

@JvmInline
value class Nickname(val value: String) {
    init {
        require(value.isNotBlank()) { "닉네임은 비어있을 수 없습니다." }
        require(value.length in MIN_LENGTH..MAX_LENGTH) {
            "닉네임은 ${MIN_LENGTH}자 이상 ${MAX_LENGTH}자 이하여야 합니다."
        }
    }

    companion object {
        const val MIN_LENGTH = 2
        const val MAX_LENGTH = 50
    }
}
