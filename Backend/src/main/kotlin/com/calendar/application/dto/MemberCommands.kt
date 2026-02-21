package com.calendar.application.dto

data class UpdateMemberCommand(
    val nickname: String? = null,
    val profileImageUrl: String? = null,
)
