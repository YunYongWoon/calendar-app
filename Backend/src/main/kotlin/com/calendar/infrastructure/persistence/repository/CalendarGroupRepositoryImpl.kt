package com.calendar.infrastructure.persistence.repository

import com.calendar.domain.model.CalendarGroup
import com.calendar.domain.model.GroupId
import com.calendar.domain.model.InviteCode
import com.calendar.domain.repository.CalendarGroupRepository
import com.calendar.infrastructure.persistence.entity.CalendarGroupEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CalendarGroupRepositoryImpl(
    private val calendarGroupJpaRepository: CalendarGroupJpaRepository,
) : CalendarGroupRepository {

    override fun save(group: CalendarGroup): CalendarGroup {
        val entity = CalendarGroupEntity.from(group)
        return calendarGroupJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: GroupId): CalendarGroup? =
        calendarGroupJpaRepository.findByIdOrNull(id.value)?.toDomain()

    override fun findByIdIn(ids: List<GroupId>): List<CalendarGroup> =
        calendarGroupJpaRepository.findAllByIdIn(ids.map { it.value }).map { it.toDomain() }

    override fun findByInviteCode(inviteCode: InviteCode): CalendarGroup? =
        calendarGroupJpaRepository.findByInviteCode(inviteCode.value)?.toDomain()

    override fun delete(group: CalendarGroup) {
        calendarGroupJpaRepository.deleteById(group.id?.value ?: return)
    }
}
