package com.calendar.application.dto

import com.calendar.domain.model.Member

data class MemberResult(
    val id: Long,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?,
    val status: String,
) {
    companion object {
        fun from(member: Member): MemberResult = MemberResult(
            id = member.id!!.value,
            email = member.email.value,
            nickname = member.nickname.value,
            profileImageUrl = member.profileImageUrl,
            status = member.status.name,
        )
    }
}
