package com.calendar.infrastructure.persistence.repository

import com.calendar.infrastructure.persistence.entity.GroupMemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface GroupMemberJpaRepository : JpaRepository<GroupMemberEntity, Long> {
    fun findByGroupIdAndMemberId(groupId: Long, memberId: Long): GroupMemberEntity?
    fun findAllByGroupId(groupId: Long): List<GroupMemberEntity>
    fun findAllByMemberId(memberId: Long): List<GroupMemberEntity>
    fun countByGroupId(groupId: Long): Int
    fun countByMemberId(memberId: Long): Int
    fun deleteAllByGroupId(groupId: Long)
}
