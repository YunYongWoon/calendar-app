package com.calendar.application.usecase

import com.calendar.application.dto.MemberResult
import com.calendar.application.dto.UpdateMemberCommand

interface MemberUseCase {
    fun getMe(memberId: Long): MemberResult
    fun updateMe(memberId: Long, command: UpdateMemberCommand): MemberResult
    fun deleteMe(memberId: Long)
}
