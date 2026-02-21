package com.calendar.interfaces.rest.controller

import com.calendar.application.dto.UpdateMemberCommand
import com.calendar.application.usecase.MemberUseCase
import com.calendar.infrastructure.security.CustomUserDetails
import com.calendar.interfaces.rest.dto.MemberResponse
import com.calendar.interfaces.rest.dto.UpdateMemberRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
class MemberController(
    private val memberUseCase: MemberUseCase,
) {

    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<MemberResponse> {
        val result = memberUseCase.getMe(userDetails.memberId)

        return ResponseEntity.ok(
            MemberResponse(
                id = result.id,
                email = result.email,
                nickname = result.nickname,
                profileImageUrl = result.profileImageUrl,
            ),
        )
    }

    @PatchMapping("/me")
    fun updateMe(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody request: UpdateMemberRequest,
    ): ResponseEntity<MemberResponse> {
        val result = memberUseCase.updateMe(
            memberId = userDetails.memberId,
            command = UpdateMemberCommand(
                nickname = request.nickname,
                profileImageUrl = request.profileImageUrl,
            ),
        )

        return ResponseEntity.ok(
            MemberResponse(
                id = result.id,
                email = result.email,
                nickname = result.nickname,
                profileImageUrl = result.profileImageUrl,
            ),
        )
    }

    @DeleteMapping("/me")
    fun deleteMe(@AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<Void> {
        memberUseCase.deleteMe(userDetails.memberId)
        return ResponseEntity.noContent().build()
    }
}
