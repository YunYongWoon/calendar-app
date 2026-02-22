package com.calendar.infrastructure.persistence.repository

import com.calendar.domain.model.GroupId
import com.calendar.domain.model.GroupMember
import com.calendar.domain.model.GroupMemberId
import com.calendar.domain.model.MemberId
import com.calendar.domain.repository.GroupMemberRepository
import com.calendar.infrastructure.persistence.entity.GroupMemberEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class GroupMemberRepositoryImpl(
    private val groupMemberJpaRepository: GroupMemberJpaRepository,
) : GroupMemberRepository {

    override fun save(groupMember: GroupMember): GroupMember {
        val entity = GroupMemberEntity.from(groupMember)
        return groupMemberJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: GroupMemberId): GroupMember? =
        groupMemberJpaRepository.findByIdOrNull(id.value)?.toDomain()

    override fun findByGroupIdAndMemberId(groupId: GroupId, memberId: MemberId): GroupMember? =
        groupMemberJpaRepository.findByGroupIdAndMemberId(groupId.value, memberId.value)?.toDomain()

    override fun findAllByGroupId(groupId: GroupId): List<GroupMember> =
        groupMemberJpaRepository.findAllByGroupId(groupId.value).map { it.toDomain() }

    override fun findAllByMemberId(memberId: MemberId): List<GroupMember> =
        groupMemberJpaRepository.findAllByMemberId(memberId.value).map { it.toDomain() }

    override fun countByGroupId(groupId: GroupId): Int =
        groupMemberJpaRepository.countByGroupId(groupId.value)

    override fun countByGroupIds(groupIds: List<GroupId>): Map<GroupId, Int> {
        if (groupIds.isEmpty()) return emptyMap()
        return groupMemberJpaRepository.countByGroupIdIn(groupIds.map { it.value })
            .associate { GroupId((it[0] as Long)) to (it[1] as Long).toInt() }
    }

    override fun countByMemberId(memberId: MemberId): Int =
        groupMemberJpaRepository.countByMemberId(memberId.value)

    override fun delete(groupMember: GroupMember) {
        groupMemberJpaRepository.deleteById(groupMember.id?.value ?: return)
    }

    override fun deleteAllByGroupId(groupId: GroupId) {
        groupMemberJpaRepository.deleteAllByGroupId(groupId.value)
    }
}
