package com.calendar.infrastructure.persistence.repository

import com.calendar.domain.model.CalendarGroup
import com.calendar.domain.model.GroupId
import com.calendar.domain.model.GroupName
import com.calendar.domain.model.GroupType
import com.calendar.domain.model.InviteCode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@Import(CalendarGroupRepositoryImpl::class)
@ActiveProfiles("test")
class CalendarGroupRepositoryImplTest(
    private val calendarGroupRepository: CalendarGroupRepositoryImpl,
) : DescribeSpec({

    extensions(SpringExtension)

    describe("save") {
        context("새 그룹을 저장하면") {
            it("ID가 할당되어 반환된다") {
                val group = CalendarGroup.create(
                    name = GroupName("테스트 그룹"),
                    type = GroupType.FAMILY,
                    description = "가족 그룹",
                )

                val saved = calendarGroupRepository.save(group)

                saved.id.shouldNotBeNull()
                saved.name.value shouldBe "테스트 그룹"
                saved.type shouldBe GroupType.FAMILY
                saved.description shouldBe "가족 그룹"
            }
        }
    }

    describe("findById") {
        context("존재하는 ID로 조회하면") {
            it("그룹을 반환한다") {
                val saved = calendarGroupRepository.save(
                    CalendarGroup.create(
                        name = GroupName("조회 테스트"),
                        type = GroupType.FRIEND,
                    ),
                )

                val found = calendarGroupRepository.findById(saved.id!!)

                found.shouldNotBeNull()
                found.name.value shouldBe "조회 테스트"
            }
        }

        context("존재하지 않는 ID로 조회하면") {
            it("null을 반환한다") {
                val found = calendarGroupRepository.findById(GroupId(99999L))

                found.shouldBeNull()
            }
        }
    }

    describe("findByInviteCode") {
        context("존재하는 초대 코드로 조회하면") {
            it("그룹을 반환한다") {
                val group = CalendarGroup(
                    name = GroupName("초대코드 테스트"),
                    type = GroupType.FRIEND,
                    inviteCode = InviteCode("ABC123"),
                    inviteCodeExpiresAt = LocalDateTime.now().plusHours(24),
                )
                calendarGroupRepository.save(group)

                val found = calendarGroupRepository.findByInviteCode(InviteCode("ABC123"))

                found.shouldNotBeNull()
                found.name.value shouldBe "초대코드 테스트"
            }
        }

        context("존재하지 않는 초대 코드로 조회하면") {
            it("null을 반환한다") {
                val found = calendarGroupRepository.findByInviteCode(InviteCode("ZZZ999"))

                found.shouldBeNull()
            }
        }
    }

    describe("delete") {
        context("그룹을 삭제하면") {
            it("더 이상 조회되지 않는다") {
                val saved = calendarGroupRepository.save(
                    CalendarGroup.create(
                        name = GroupName("삭제 테스트"),
                        type = GroupType.CUSTOM,
                    ),
                )

                calendarGroupRepository.delete(saved)

                val found = calendarGroupRepository.findById(saved.id!!)
                found.shouldBeNull()
            }
        }
    }
})
