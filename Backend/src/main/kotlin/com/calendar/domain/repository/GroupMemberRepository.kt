package com.calendar.domain.repository

import com.calendar.domain.model.GroupId
import com.calendar.domain.model.GroupMember
import com.calendar.domain.model.GroupMemberId
import com.calendar.domain.model.MemberId

interface GroupMemberRepository {
    fun save(groupMember: GroupMember): GroupMember
    fun findById(id: GroupMemberId): GroupMember?
    fun findByGroupIdAndMemberId(groupId: GroupId, memberId: MemberId): GroupMember?
    fun findAllByGroupId(groupId: GroupId): List<GroupMember>
    fun findAllByMemberId(memberId: MemberId): List<GroupMember>
    fun countByGroupId(groupId: GroupId): Int
    fun countByGroupIds(groupIds: List<GroupId>): Map<GroupId, Int>
    fun countByMemberId(memberId: MemberId): Int
    fun delete(groupMember: GroupMember)
    fun deleteAllByGroupId(groupId: GroupId)
}
