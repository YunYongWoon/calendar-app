package com.calendar.interfaces.rest.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateGroupRequest(
    @field:NotBlank(message = "그룹 이름은 필수입니다.")
    @field:Size(max = 100, message = "그룹 이름은 100자 이하여야 합니다.")
    val name: String,

    @field:NotBlank(message = "그룹 유형은 필수입니다.")
    val type: String,

    @field:Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    val description: String? = null,

    val coverImageUrl: String? = null,
)

data class UpdateGroupRequest(
    @field:Size(max = 100, message = "그룹 이름은 100자 이하여야 합니다.")
    val name: String? = null,

    @field:Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    val description: String? = null,

    val coverImageUrl: String? = null,
)

data class JoinGroupRequest(
    @field:NotBlank(message = "초대 코드는 필수입니다.")
    @field:Pattern(regexp = "^[A-Za-z0-9]{6}$", message = "초대 코드는 6자리 영숫자여야 합니다.")
    val inviteCode: String,
)

data class UpdateGroupMemberRequest(
    val role: String? = null,

    @field:Size(max = 50, message = "표시 이름은 50자 이하여야 합니다.")
    val displayName: String? = null,

    @field:Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "올바른 색상 코드 형식이 아닙니다. (예: #FF0000)")
    val color: String? = null,
)

data class GroupResponse(
    val id: Long,
    val name: String,
    val type: String,
    val description: String?,
    val coverImageUrl: String?,
    val maxMembers: Int,
    val memberCount: Int,
)

data class GroupMemberResponse(
    val id: Long,
    val groupId: Long,
    val memberId: Long,
    val role: String,
    val displayName: String?,
    val color: String?,
)

data class InviteCodeResponse(
    val inviteCode: String,
    val expiresAt: String,
)
