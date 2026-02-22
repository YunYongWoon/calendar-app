package com.calendar.infrastructure.persistence.repository

import com.calendar.infrastructure.persistence.entity.GroupMemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GroupMemberJpaRepository : JpaRepository<GroupMemberEntity, Long> {
    fun findByGroupIdAndMemberId(groupId: Long, memberId: Long): GroupMemberEntity?
    fun findAllByGroupId(groupId: Long): List<GroupMemberEntity>
    fun findAllByMemberId(memberId: Long): List<GroupMemberEntity>
    fun countByGroupId(groupId: Long): Int
    fun countByMemberId(memberId: Long): Int

    @Query("SELECT gm.groupId, COUNT(gm) FROM GroupMemberEntity gm WHERE gm.groupId IN :groupIds GROUP BY gm.groupId")
    fun countByGroupIdIn(@Param("groupIds") groupIds: List<Long>): List<Array<Any>>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM GroupMemberEntity gm WHERE gm.groupId = :groupId")
    fun deleteAllByGroupId(@Param("groupId") groupId: Long)
}
