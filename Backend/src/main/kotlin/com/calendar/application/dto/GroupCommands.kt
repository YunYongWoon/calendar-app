package com.calendar.application.dto

data class CreateGroupCommand(
    val name: String,
    val type: String,
    val description: String? = null,
    val coverImageUrl: String? = null,
)

data class UpdateGroupCommand(
    val name: String? = null,
    val description: String? = null,
    val clearDescription: Boolean = false,
    val coverImageUrl: String? = null,
    val clearCoverImageUrl: Boolean = false,
)

data class JoinGroupCommand(
    val inviteCode: String,
)

data class UpdateGroupMemberCommand(
    val role: String? = null,
    val displayName: String? = null,
    val clearDisplayName: Boolean = false,
    val color: String? = null,
    val clearColor: Boolean = false,
)
