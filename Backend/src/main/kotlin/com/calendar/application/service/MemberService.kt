package com.calendar.application.service

import com.calendar.application.dto.MemberResult
import com.calendar.application.dto.UpdateMemberCommand
import com.calendar.application.usecase.MemberUseCase
import com.calendar.domain.exception.MemberNotFoundException
import com.calendar.domain.model.MemberId
import com.calendar.domain.model.Nickname
import com.calendar.domain.repository.MemberRepository
import com.calendar.domain.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) : MemberUseCase {

    override fun getMe(memberId: Long): MemberResult {
        val member = memberRepository.findById(MemberId(memberId))
            ?: throw MemberNotFoundException()

        if (!member.isActive) {
            throw MemberNotFoundException()
        }

        return MemberResult.from(member)
    }

    @Transactional
    override fun updateMe(memberId: Long, command: UpdateMemberCommand): MemberResult {
        val member = memberRepository.findById(MemberId(memberId))
            ?: throw MemberNotFoundException()

        if (!member.isActive) {
            throw MemberNotFoundException()
        }

        val updatedMember = member.updateProfile(
            nickname = command.nickname?.let { Nickname(it) },
            profileImageUrl = command.profileImageUrl,
        )

        val saved = memberRepository.save(updatedMember)
        return MemberResult.from(saved)
    }

    @Transactional
    override fun deleteMe(memberId: Long) {
        val member = memberRepository.findById(MemberId(memberId))
            ?: throw MemberNotFoundException()

        val withdrawn = member.withdraw()
        memberRepository.save(withdrawn)
        refreshTokenRepository.deleteAllByMemberId(MemberId(memberId))
    }
}
