package com.calendar.interfaces.rest.controller

import com.calendar.application.dto.CreateGroupCommand
import com.calendar.application.dto.JoinGroupCommand
import com.calendar.application.dto.UpdateGroupCommand
import com.calendar.application.dto.UpdateGroupMemberCommand
import com.calendar.application.usecase.GroupUseCase
import com.calendar.infrastructure.security.CustomUserDetails
import com.calendar.interfaces.rest.dto.CreateGroupRequest
import com.calendar.interfaces.rest.dto.GroupMemberResponse
import com.calendar.interfaces.rest.dto.GroupResponse
import com.calendar.interfaces.rest.dto.InviteCodeResponse
import com.calendar.interfaces.rest.dto.JoinGroupRequest
import com.calendar.interfaces.rest.dto.UpdateGroupMemberRequest
import com.calendar.interfaces.rest.dto.UpdateGroupRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/groups")
class GroupController(
    private val groupUseCase: GroupUseCase,
) {

    @PostMapping
    fun createGroup(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody request: CreateGroupRequest,
    ): ResponseEntity<GroupResponse> {
        val result = groupUseCase.createGroup(
            memberId = userDetails.memberId,
            command = CreateGroupCommand(
                name = request.name,
                type = request.type,
                description = request.description,
                coverImageUrl = request.coverImageUrl,
            ),
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(
            GroupResponse(
                id = result.id,
                name = result.name,
                type = result.type,
                description = result.description,
                coverImageUrl = result.coverImageUrl,
                maxMembers = result.maxMembers,
                memberCount = result.memberCount,
            ),
        )
    }

    @GetMapping
    fun getMyGroups(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ResponseEntity<List<GroupResponse>> {
        val results = groupUseCase.getMyGroups(userDetails.memberId)

        return ResponseEntity.ok(
            results.map {
                GroupResponse(
                    id = it.id,
                    name = it.name,
                    type = it.type,
                    description = it.description,
                    coverImageUrl = it.coverImageUrl,
                    maxMembers = it.maxMembers,
                    memberCount = it.memberCount,
                )
            },
        )
    }

    @GetMapping("/{id}")
    fun getGroup(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<GroupResponse> {
        val result = groupUseCase.getGroup(userDetails.memberId, id)

        return ResponseEntity.ok(
            GroupResponse(
                id = result.id,
                name = result.name,
                type = result.type,
                description = result.description,
                coverImageUrl = result.coverImageUrl,
                maxMembers = result.maxMembers,
                memberCount = result.memberCount,
            ),
        )
    }

    @PatchMapping("/{id}")
    fun updateGroup(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateGroupRequest,
    ): ResponseEntity<GroupResponse> {
        val result = groupUseCase.updateGroup(
            memberId = userDetails.memberId,
            groupId = id,
            command = UpdateGroupCommand(
                name = request.name,
                description = request.description,
                coverImageUrl = request.coverImageUrl,
            ),
        )

        return ResponseEntity.ok(
            GroupResponse(
                id = result.id,
                name = result.name,
                type = result.type,
                description = result.description,
                coverImageUrl = result.coverImageUrl,
                maxMembers = result.maxMembers,
                memberCount = result.memberCount,
            ),
        )
    }

    @DeleteMapping("/{id}")
    fun deleteGroup(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        groupUseCase.deleteGroup(userDetails.memberId, id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/invite-code")
    fun generateInviteCode(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<InviteCodeResponse> {
        val result = groupUseCase.generateInviteCode(userDetails.memberId, id)

        return ResponseEntity.ok(
            InviteCodeResponse(
                inviteCode = result.inviteCode,
                expiresAt = result.expiresAt,
            ),
        )
    }

    @PostMapping("/join")
    fun joinGroup(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody request: JoinGroupRequest,
    ): ResponseEntity<GroupMemberResponse> {
        val result = groupUseCase.joinGroup(
            memberId = userDetails.memberId,
            command = JoinGroupCommand(inviteCode = request.inviteCode),
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(
            GroupMemberResponse(
                id = result.id,
                groupId = result.groupId,
                memberId = result.memberId,
                role = result.role,
                displayName = result.displayName,
                color = result.color,
            ),
        )
    }

    @GetMapping("/{id}/members")
    fun getGroupMembers(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<List<GroupMemberResponse>> {
        val results = groupUseCase.getGroupMembers(userDetails.memberId, id)

        return ResponseEntity.ok(
            results.map {
                GroupMemberResponse(
                    id = it.id,
                    groupId = it.groupId,
                    memberId = it.memberId,
                    role = it.role,
                    displayName = it.displayName,
                    color = it.color,
                )
            },
        )
    }

    @PatchMapping("/{id}/members/{memberId}")
    fun updateGroupMember(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable id: Long,
        @PathVariable memberId: Long,
        @Valid @RequestBody request: UpdateGroupMemberRequest,
    ): ResponseEntity<GroupMemberResponse> {
        val result = groupUseCase.updateGroupMember(
            memberId = userDetails.memberId,
            groupId = id,
            targetMemberId = memberId,
            command = UpdateGroupMemberCommand(
                role = request.role,
                displayName = request.displayName,
                color = request.color,
            ),
        )

        return ResponseEntity.ok(
            GroupMemberResponse(
                id = result.id,
                groupId = result.groupId,
                memberId = result.memberId,
                role = result.role,
                displayName = result.displayName,
                color = result.color,
            ),
        )
    }

    @DeleteMapping("/{id}/members/{memberId}")
    fun removeGroupMember(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable id: Long,
        @PathVariable memberId: Long,
    ): ResponseEntity<Void> {
        groupUseCase.removeGroupMember(userDetails.memberId, id, memberId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}/members/me")
    fun leaveGroup(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        groupUseCase.leaveGroup(userDetails.memberId, id)
        return ResponseEntity.noContent().build()
    }
}
