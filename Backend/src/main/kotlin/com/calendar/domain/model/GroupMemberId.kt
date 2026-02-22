package com.calendar.domain.model

@JvmInline
value class GroupMemberId(val value: Long) {
    init {
        require(value > 0) { "GroupMemberId는 양수여야 합니다." }
    }
}
