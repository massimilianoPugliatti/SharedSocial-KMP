package com.example.sharedsocial_kmp.features.register.domain.validation

import com.example.sharedsocial_kmp.features.register.domain.model.ValidationReason

/**
 * Contiene le regole di validazione centralizzate per la registrazione.
 * Assicura che la logica sia coerente tra UI e Business Logic.
 */
object RegisterValidator {
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

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            password != confirmPassword -> ValidationResult.Invalid(ValidationReason.NOT_MATCH)
            else -> ValidationResult.Valid
        }
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val reason: ValidationReason) : ValidationResult()
}

