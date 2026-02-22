package com.calendar.domain.repository

import com.calendar.domain.model.CalendarGroup
import com.calendar.domain.model.GroupId
import com.calendar.domain.model.InviteCode

interface CalendarGroupRepository {
    fun save(group: CalendarGroup): CalendarGroup
    fun findById(id: GroupId): CalendarGroup?
    fun findByInviteCode(inviteCode: InviteCode): CalendarGroup?
    fun delete(group: CalendarGroup)
}
