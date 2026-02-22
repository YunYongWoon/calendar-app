package com.calendar.domain.model

@JvmInline
value class GroupName(val value: String) {
    init {
        require(value.isNotBlank()) { "그룹 이름은 비어있을 수 없습니다." }
        require(value.length <= MAX_LENGTH) { "그룹 이름은 ${MAX_LENGTH}자 이하여야 합니다." }
    }

    companion object {
        const val MAX_LENGTH = 100
    }
}
