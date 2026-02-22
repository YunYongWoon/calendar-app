package com.calendar.interfaces.rest.dto

import com.calendar.application.dto.GroupMemberResult
import com.calendar.application.dto.GroupResult
import com.calendar.application.dto.InviteCodeResult
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateGroupRequest(
    @field:NotBlank(message = "그룹 이름은 필수입니다.")
    @field:Size(max = 100, message = "그룹 이름은 100자 이하여야 합니다.")
    val name: String,

    @field:NotBlank(message = "그룹 유형은 필수입니다.")
    @field:Pattern(
        regexp = "^(COUPLE|FRIEND|FAMILY|CUSTOM)$",
        message = "그룹 유형은 COUPLE, FRIEND, FAMILY, CUSTOM 중 하나여야 합니다.",
    )
    val type: String,

    @field:Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    val description: String? = null,

    @field:Size(max = 500, message = "커버 이미지 URL은 500자 이하여야 합니다.")
    @field:Pattern(
        regexp = "^https?://.*$",
        message = "커버 이미지 URL은 http 또는 https 프로토콜만 허용됩니다.",
    )
    val coverImageUrl: String? = null,
)

data class UpdateGroupRequest(
    @field:Size(max = 100, message = "그룹 이름은 100자 이하여야 합니다.")
    val name: String? = null,

    @field:Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    val description: String? = null,
    val clearDescription: Boolean = false,

    @field:Size(max = 500, message = "커버 이미지 URL은 500자 이하여야 합니다.")
    @field:Pattern(
        regexp = "^https?://.*$",
        message = "커버 이미지 URL은 http 또는 https 프로토콜만 허용됩니다.",
    )
    val coverImageUrl: String? = null,
    val clearCoverImageUrl: Boolean = false,
)

data class JoinGroupRequest(
    @field:NotBlank(message = "초대 코드는 필수입니다.")
    @field:Pattern(regexp = "^[A-Za-z0-9]{6}$", message = "초대 코드는 6자리 영숫자여야 합니다.")
    val inviteCode: String,
)

data class UpdateGroupMemberRequest(
    @field:Pattern(
        regexp = "^(ADMIN|MEMBER)$",
        message = "역할은 ADMIN 또는 MEMBER만 지정할 수 있습니다.",
    )
    val role: String? = null,

    @field:Size(max = 50, message = "표시 이름은 50자 이하여야 합니다.")
    val displayName: String? = null,
    val clearDisplayName: Boolean = false,

    @field:Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "올바른 색상 코드 형식이 아닙니다. (예: #FF0000)")
    val color: String? = null,
    val clearColor: Boolean = false,
)

data class GroupResponse(
    val id: Long,
    val name: String,
    val type: String,
    val description: String?,
    val coverImageUrl: String?,
    val maxMembers: Int,
    val memberCount: Int,
) {
    companion object {
        fun from(result: GroupResult): GroupResponse = GroupResponse(
            id = result.id,
            name = result.name,
            type = result.type,
            description = result.description,
            coverImageUrl = result.coverImageUrl,
            maxMembers = result.maxMembers,
            memberCount = result.memberCount,
        )
    }
}

data class GroupMemberResponse(
    val id: Long,
    val groupId: Long,
    val memberId: Long,
    val role: String,
    val displayName: String?,
    val color: String?,
) {
    companion object {
        fun from(result: GroupMemberResult): GroupMemberResponse = GroupMemberResponse(
            id = result.id,
            groupId = result.groupId,
            memberId = result.memberId,
            role = result.role,
            displayName = result.displayName,
            color = result.color,
        )
    }
}

data class InviteCodeResponse(
    val inviteCode: String,
    val expiresAt: LocalDateTime,
) {
    companion object {
        fun from(result: InviteCodeResult): InviteCodeResponse = InviteCodeResponse(
            inviteCode = result.inviteCode,
            expiresAt = result.expiresAt,
        )
    }
}
