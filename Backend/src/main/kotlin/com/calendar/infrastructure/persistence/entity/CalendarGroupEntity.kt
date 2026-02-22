package com.calendar.infrastructure.persistence.entity

import com.calendar.domain.model.CalendarGroup
import com.calendar.domain.model.GroupId
import com.calendar.domain.model.GroupName
import com.calendar.domain.model.GroupType
import com.calendar.domain.model.InviteCode
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
@Table(name = "calendar_group")
class CalendarGroupEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val type: GroupType,

    @Column(length = 500)
    var description: String? = null,

    @Column(name = "cover_image_url", length = 500)
    var coverImageUrl: String? = null,

    @Column(name = "invite_code", length = 10)
    var inviteCode: String? = null,

    @Column(name = "invite_code_expires_at")
    var inviteCodeExpiresAt: LocalDateTime? = null,

    @Column(name = "max_members", nullable = false)
    val maxMembers: Int = 50,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): CalendarGroup = CalendarGroup(
        id = if (id > 0) GroupId(id) else null,
        name = GroupName(name),
        type = type,
        description = description,
        coverImageUrl = coverImageUrl,
        inviteCode = inviteCode?.let { InviteCode(it) },
        inviteCodeExpiresAt = inviteCodeExpiresAt,
        maxMembers = maxMembers,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun from(group: CalendarGroup): CalendarGroupEntity = CalendarGroupEntity(
            id = group.id?.value ?: 0,
            name = group.name.value,
            type = group.type,
            description = group.description,
            coverImageUrl = group.coverImageUrl,
            inviteCode = group.inviteCode?.value,
            inviteCodeExpiresAt = group.inviteCodeExpiresAt,
            maxMembers = group.maxMembers,
            createdAt = group.createdAt,
            updatedAt = group.updatedAt,
        )
    }
}
