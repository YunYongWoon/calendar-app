package com.calendar.domain.model

@JvmInline
value class DisplayName(val value: String) {
    init {
        require(value.isNotBlank()) { "표시 이름은 비어있을 수 없습니다." }
        require(value.length <= MAX_LENGTH) { "표시 이름은 ${MAX_LENGTH}자 이하여야 합니다." }
    }

    companion object {
        const val MAX_LENGTH = 50
    }
}
