package com.calendar.domain.model

@JvmInline
value class GroupId(val value: Long) {
    init {
        require(value > 0) { "GroupId는 양수여야 합니다." }
    }
}
