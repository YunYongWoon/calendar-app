package com.calendar.application.service

import com.calendar.application.dto.UpdateMemberCommand
import com.calendar.domain.exception.MemberNotFoundException
import com.calendar.domain.model.Email
import com.calendar.domain.model.Member
import com.calendar.domain.model.MemberId
import com.calendar.domain.model.MemberStatus
import com.calendar.domain.model.Nickname
import com.calendar.domain.model.Password
import com.calendar.domain.repository.MemberRepository
import com.calendar.domain.repository.RefreshTokenRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class MemberServiceTest : DescribeSpec({

    val memberRepository = mockk<MemberRepository>()
    val refreshTokenRepository = mockk<RefreshTokenRepository>()

    val memberService = MemberService(
        memberRepository = memberRepository,
        refreshTokenRepository = refreshTokenRepository,
    )

    val activeMember = Member(
        id = MemberId(1L),
        email = Email("user@example.com"),
        password = Password("encodedPassword"),
        nickname = Nickname("기존회원"),
        status = MemberStatus.ACTIVE,
    )

    describe("getMe") {
        context("존재하는 활성 회원이면") {
            it("회원 정보를 반환한다") {
                every { memberRepository.findById(MemberId(1L)) } returns activeMember

                val result = memberService.getMe(1L)

                result.email shouldBe "user@example.com"
                result.nickname shouldBe "기존회원"
            }
        }

        context("존재하지 않는 회원이면") {
            it("MemberNotFoundException이 발생한다") {
                every { memberRepository.findById(MemberId(999L)) } returns null

                shouldThrow<MemberNotFoundException> {
                    memberService.getMe(999L)
                }
            }
        }

        context("탈퇴한 회원이면") {
            it("MemberNotFoundException이 발생한다") {
                val deletedMember = activeMember.copy(status = MemberStatus.DELETED)
                every { memberRepository.findById(MemberId(1L)) } returns deletedMember

                shouldThrow<MemberNotFoundException> {
                    memberService.getMe(1L)
                }
            }
        }
    }

    describe("updateMe") {
        context("닉네임을 변경하면") {
            it("변경된 회원 정보를 반환한다") {
                every { memberRepository.findById(MemberId(1L)) } returns activeMember
                every { memberRepository.save(any()) } answers { firstArg() }

                val result = memberService.updateMe(1L, UpdateMemberCommand(nickname = "새닉네임"))

                result.nickname shouldBe "새닉네임"
            }
        }

        context("존재하지 않는 회원이면") {
            it("MemberNotFoundException이 발생한다") {
                every { memberRepository.findById(MemberId(999L)) } returns null

                shouldThrow<MemberNotFoundException> {
                    memberService.updateMe(999L, UpdateMemberCommand(nickname = "새닉네임"))
                }
            }
        }
    }

    describe("deleteMe") {
        context("존재하는 활성 회원이면") {
            it("DELETED 상태로 변경하고 리프레시 토큰을 삭제한다") {
                every { memberRepository.findById(MemberId(1L)) } returns activeMember
                every { memberRepository.save(any()) } answers { firstArg() }
                justRun { refreshTokenRepository.deleteAllByMemberId(MemberId(1L)) }

                memberService.deleteMe(1L)

                verify {
                    memberRepository.save(match { it.status == MemberStatus.DELETED })
                    refreshTokenRepository.deleteAllByMemberId(MemberId(1L))
                }
            }
        }

        context("존재하지 않는 회원이면") {
            it("MemberNotFoundException이 발생한다") {
                every { memberRepository.findById(MemberId(999L)) } returns null

                shouldThrow<MemberNotFoundException> {
                    memberService.deleteMe(999L)
                }
            }
        }
    }
})
