package com.example.sharedsocial_kmp.features.auth.domain.validation

import com.example.sharedsocial_kmp.features.auth.domain.model.ValidationReason

/**
 * Contiene le regole di validazione centralizzate per l'autenticazione.
 * Assicura che la logica sia coerente tra UI e Business Logic.
 */

object AuthValidator {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$")
    private const val MIN_PASSWORD_LENGTH = 6


    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid(ValidationReason.EMPTY)
            !email.matches(EMAIL_REGEX) -> ValidationResult.Invalid(ValidationReason.INVALID_FORMAT)
            else -> ValidationResult.Valid
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Invalid(ValidationReason.EMPTY)
            password.length < MIN_PASSWORD_LENGTH -> ValidationResult.Invalid(ValidationReason.TOO_SHORT)
            else -> ValidationResult.Valid
        }
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val reason: ValidationReason) : ValidationResult()
}

