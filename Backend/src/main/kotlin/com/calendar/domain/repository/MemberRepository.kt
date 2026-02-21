package com.calendar.domain.repository

import com.calendar.domain.model.Email
import com.calendar.domain.model.Member
import com.calendar.domain.model.MemberId

interface MemberRepository {
    fun save(member: Member): Member
    fun findById(id: MemberId): Member?
    fun findByEmail(email: Email): Member?
    fun existsByEmail(email: Email): Boolean
}
