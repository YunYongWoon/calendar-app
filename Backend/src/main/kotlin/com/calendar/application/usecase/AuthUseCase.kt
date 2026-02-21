package com.calendar.application.usecase

import com.calendar.application.dto.AuthResult
import com.calendar.application.dto.LoginCommand
import com.calendar.application.dto.MemberResult
import com.calendar.application.dto.RefreshCommand
import com.calendar.application.dto.SignupCommand

interface AuthUseCase {
    fun signup(command: SignupCommand): MemberResult
    fun login(command: LoginCommand): AuthResult
    fun refresh(command: RefreshCommand): AuthResult
    fun logout(refreshToken: String)
}
