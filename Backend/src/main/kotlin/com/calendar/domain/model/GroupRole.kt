package com.calendar.domain.model

enum class GroupRole {
    OWNER,
    ADMIN,
    MEMBER,
    ;

    fun canManage(): Boolean = this == OWNER || this == ADMIN

    fun isOwner(): Boolean = this == OWNER
}
