package com.calendar.infrastructure.persistence.entity

import com.calendar.domain.model.Email
import com.calendar.domain.model.Member
import com.calendar.domain.model.MemberId
import com.calendar.domain.model.MemberStatus
import com.calendar.domain.model.Nickname
import com.calendar.domain.model.Password
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
@Table(name = "member")
class MemberEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false, length = 50)
    var nickname: String,

    @Column(name = "profile_image_url", length = 500)
    var profileImageUrl: String? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: MemberStatus = MemberStatus.ACTIVE,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): Member = Member(
        id = MemberId(id),
        email = Email(email),
        password = Password(password),
        nickname = Nickname(nickname),
        profileImageUrl = profileImageUrl,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun from(member: Member): MemberEntity = MemberEntity(
            id = member.id?.value ?: 0,
            email = member.email.value,
            password = member.password.value,
            nickname = member.nickname.value,
            profileImageUrl = member.profileImageUrl,
            status = member.status,
            createdAt = member.createdAt,
            updatedAt = member.updatedAt,
        )
    }
}
