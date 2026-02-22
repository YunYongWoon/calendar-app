package com.calendar.infrastructure.persistence.repository

import com.calendar.infrastructure.persistence.entity.CalendarGroupEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CalendarGroupJpaRepository : JpaRepository<CalendarGroupEntity, Long> {
    fun findByInviteCode(inviteCode: String): CalendarGroupEntity?
    fun findAllByIdIn(ids: List<Long>): List<CalendarGroupEntity>
}
