package com.calendar.infrastructure.persistence.entity

import com.calendar.domain.model.ColorHex
import com.calendar.domain.model.DisplayName
import com.calendar.domain.model.GroupId
import com.calendar.domain.model.GroupMember
import com.calendar.domain.model.GroupMemberId
import com.calendar.domain.model.GroupRole
import com.calendar.domain.model.MemberId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "group_member")
class GroupMemberEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "group_id", nullable = false)
    val groupId: Long,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var role: GroupRole = GroupRole.MEMBER,

    @Column(name = "display_name", length = 50)
    var displayName: String? = null,

    @Column(length = 7)
    var color: String? = null,

    @Column(name = "joined_at", nullable = false, updatable = false)
    val joinedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): GroupMember = GroupMember(
        id = if (id > 0) GroupMemberId(id) else null,
        groupId = GroupId(groupId),
        memberId = MemberId(memberId),
        role = role,
        displayName = displayName?.let { DisplayName(it) },
        color = color?.let { ColorHex(it) },
        joinedAt = joinedAt,
    )

    companion object {
        fun from(groupMember: GroupMember): GroupMemberEntity = GroupMemberEntity(
            id = groupMember.id?.value ?: 0,
            groupId = groupMember.groupId.value,
            memberId = groupMember.memberId.value,
            role = groupMember.role,
            displayName = groupMember.displayName?.value,
            color = groupMember.color?.value,
            joinedAt = groupMember.joinedAt,
        )
    }
}
