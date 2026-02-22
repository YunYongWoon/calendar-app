package com.calendar.infrastructure.persistence.repository

import com.calendar.domain.model.CalendarGroup
import com.calendar.domain.model.ColorHex
import com.calendar.domain.model.DisplayName
import com.calendar.domain.model.Email
import com.calendar.domain.model.GroupId
import com.calendar.domain.model.GroupMember
import com.calendar.domain.model.GroupName
import com.calendar.domain.model.GroupRole
import com.calendar.domain.model.GroupType
import com.calendar.domain.model.Member
import com.calendar.domain.model.MemberId
import com.calendar.domain.model.Nickname
import com.calendar.domain.model.Password
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@Import(
    GroupMemberRepositoryImpl::class,
    CalendarGroupRepositoryImpl::class,
    MemberRepositoryImpl::class,
)
@ActiveProfiles("test")
class GroupMemberRepositoryImplTest(
    private val groupMemberRepository: GroupMemberRepositoryImpl,
    private val calendarGroupRepository: CalendarGroupRepositoryImpl,
    private val memberRepository: MemberRepositoryImpl,
) : DescribeSpec({

    extensions(SpringExtension)

    fun createTestMember(suffix: String): Member {
        return memberRepository.save(
            Member.create(
                email = Email("gm-test-$suffix@example.com"),
                password = Password("hashedPassword"),
                nickname = Nickname("테스터$suffix"),
            ),
        )
    }

    fun createTestGroup(suffix: String): CalendarGroup {
        return calendarGroupRepository.save(
            CalendarGroup.create(
                name = GroupName("그룹$suffix"),
                type = GroupType.FRIEND,
            ),
        )
    }

    describe("save") {
        context("새 그룹 멤버를 저장하면") {
            it("ID가 할당되어 반환된다") {
                val member = createTestMember("save")
                val group = createTestGroup("save")
                val groupMember = GroupMember.createOwner(group.id!!, member.id!!)

                val saved = groupMemberRepository.save(groupMember)

                saved.id.shouldNotBeNull()
                saved.groupId shouldBe group.id
                saved.memberId shouldBe member.id
                saved.role shouldBe GroupRole.OWNER
            }
        }
    }

    describe("findByGroupIdAndMemberId") {
        context("존재하는 조합으로 조회하면") {
            it("그룹 멤버를 반환한다") {
                val member = createTestMember("findComb")
                val group = createTestGroup("findComb")
                groupMemberRepository.save(GroupMember.createMember(group.id!!, member.id!!))

                val found = groupMemberRepository.findByGroupIdAndMemberId(group.id!!, member.id!!)

                found.shouldNotBeNull()
                found.role shouldBe GroupRole.MEMBER
            }
        }

        context("존재하지 않는 조합으로 조회하면") {
            it("null을 반환한다") {
                val found = groupMemberRepository.findByGroupIdAndMemberId(
                    GroupId(99999L),
                    MemberId(99999L),
                )

                found.shouldBeNull()
            }
        }
    }

    describe("findAllByGroupId") {
        context("그룹에 멤버가 있으면") {
            it("모든 멤버를 반환한다") {
                val member1 = createTestMember("allByGroup1")
                val member2 = createTestMember("allByGroup2")
                val group = createTestGroup("allByGroup")
                groupMemberRepository.save(GroupMember.createOwner(group.id!!, member1.id!!))
                groupMemberRepository.save(GroupMember.createMember(group.id!!, member2.id!!))

                val members = groupMemberRepository.findAllByGroupId(group.id!!)

                members shouldHaveSize 2
            }
        }
    }

    describe("findAllByMemberId") {
        context("멤버가 여러 그룹에 속해 있으면") {
            it("모든 그룹 멤버 정보를 반환한다") {
                val member = createTestMember("allByMember")
                val group1 = createTestGroup("allByMember1")
                val group2 = createTestGroup("allByMember2")
                groupMemberRepository.save(GroupMember.createOwner(group1.id!!, member.id!!))
                groupMemberRepository.save(GroupMember.createMember(group2.id!!, member.id!!))

                val groupMembers = groupMemberRepository.findAllByMemberId(member.id!!)

                groupMembers shouldHaveSize 2
            }
        }
    }

    describe("countByGroupId") {
        it("그룹의 멤버 수를 반환한다") {
            val member1 = createTestMember("countGroup1")
            val member2 = createTestMember("countGroup2")
            val group = createTestGroup("countGroup")
            groupMemberRepository.save(GroupMember.createOwner(group.id!!, member1.id!!))
            groupMemberRepository.save(GroupMember.createMember(group.id!!, member2.id!!))

            val count = groupMemberRepository.countByGroupId(group.id!!)

            count shouldBe 2
        }
    }

    describe("countByMemberId") {
        it("멤버가 속한 그룹 수를 반환한다") {
            val member = createTestMember("countMember")
            val group1 = createTestGroup("countMember1")
            val group2 = createTestGroup("countMember2")
            groupMemberRepository.save(GroupMember.createOwner(group1.id!!, member.id!!))
            groupMemberRepository.save(GroupMember.createMember(group2.id!!, member.id!!))

            val count = groupMemberRepository.countByMemberId(member.id!!)

            count shouldBe 2
        }
    }

    describe("delete") {
        context("그룹 멤버를 삭제하면") {
            it("더 이상 조회되지 않는다") {
                val member = createTestMember("delete")
                val group = createTestGroup("delete")
                val saved = groupMemberRepository.save(GroupMember.createMember(group.id!!, member.id!!))

                groupMemberRepository.delete(saved)

                val found = groupMemberRepository.findByGroupIdAndMemberId(group.id!!, member.id!!)
                found.shouldBeNull()
            }
        }
    }

    describe("deleteAllByGroupId") {
        context("그룹의 모든 멤버를 삭제하면") {
            it("해당 그룹에 멤버가 없어진다") {
                val member1 = createTestMember("deleteAll1")
                val member2 = createTestMember("deleteAll2")
                val group = createTestGroup("deleteAll")
                groupMemberRepository.save(GroupMember.createOwner(group.id!!, member1.id!!))
                groupMemberRepository.save(GroupMember.createMember(group.id!!, member2.id!!))

                groupMemberRepository.deleteAllByGroupId(group.id!!)

                val members = groupMemberRepository.findAllByGroupId(group.id!!)
                members shouldHaveSize 0
            }
        }
    }
})
