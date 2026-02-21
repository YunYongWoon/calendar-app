package com.calendar.infrastructure.persistence.repository

import com.calendar.domain.model.Email
import com.calendar.domain.model.Member
import com.calendar.domain.model.MemberId
import com.calendar.domain.repository.MemberRepository
import com.calendar.infrastructure.persistence.entity.MemberEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryImpl(
    private val memberJpaRepository: MemberJpaRepository,
) : MemberRepository {

    override fun save(member: Member): Member {
        val entity = MemberEntity.from(member)
        return memberJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: MemberId): Member? =
        memberJpaRepository.findByIdOrNull(id.value)?.toDomain()

    override fun findByEmail(email: Email): Member? =
        memberJpaRepository.findByEmail(email.value)?.toDomain()

    override fun existsByEmail(email: Email): Boolean =
        memberJpaRepository.existsByEmail(email.value)
}
