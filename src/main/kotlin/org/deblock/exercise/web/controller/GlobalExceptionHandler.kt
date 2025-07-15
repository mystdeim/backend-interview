package org.deblock.exercise.web.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.deblock.exercise.web.dto.ErrorResponseDto
import jakarta.validation.ConstraintViolationException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponseDto> =
        errorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.message ?: "Illegal argument")

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponseDto> {
        val errors = ex.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }

        return errorResponse(HttpStatus.BAD_REQUEST, "Validation Failed", errors)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ErrorResponseDto> {
        val errors = ex.constraintViolations.joinToString("; ") { "${it.propertyPath}: ${it.message}" }
        return errorResponse(HttpStatus.BAD_REQUEST, "Constraint Violation", errors)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponseDto> =
        errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.message ?: "Unexpected error")

    private fun errorResponse(
        status: HttpStatus,
        error: String,
        message: String,
    ): ResponseEntity<ErrorResponseDto> =
        ResponseEntity.status(status)
            .body(ErrorResponseDto(error = error, message = message))
}
