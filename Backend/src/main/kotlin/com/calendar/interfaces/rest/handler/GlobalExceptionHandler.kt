package com.calendar.interfaces.rest.handler

import com.calendar.domain.exception.CalendarException
import com.calendar.domain.exception.DuplicateEmailException
import com.calendar.domain.exception.ExpiredTokenException
import com.calendar.domain.exception.InvalidCredentialsException
import com.calendar.domain.exception.InvalidTokenException
import com.calendar.domain.exception.MemberNotFoundException
import com.calendar.interfaces.rest.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(CalendarException::class)
    fun handleCalendarException(e: CalendarException): ResponseEntity<ErrorResponse> {
        val status = when (e) {
            is MemberNotFoundException -> HttpStatus.NOT_FOUND
            is DuplicateEmailException -> HttpStatus.CONFLICT
            is InvalidCredentialsException -> HttpStatus.UNAUTHORIZED
            is InvalidTokenException -> HttpStatus.UNAUTHORIZED
            is ExpiredTokenException -> HttpStatus.UNAUTHORIZED
        }

        return ResponseEntity
            .status(status)
            .body(ErrorResponse(errorCode = e.errorCode, message = e.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = e.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(errorCode = "VALIDATION_ERROR", message = message))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(errorCode = "BAD_REQUEST", message = e.message ?: "잘못된 요청입니다."))
    }
}
