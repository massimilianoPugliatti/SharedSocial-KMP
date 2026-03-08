package com.example.sharedsocial_kmp.features.register.domain.model


sealed class RegisterError : Throwable() {
    data class InvalidRequest(override val message: String? = "Invalid Request") :
        RegisterError()

    data class InvalidApiKey(override val message: String? = "Invalid API Key") : RegisterError()
    data class Forbidden(override val message: String? = "Forbidden Access") : RegisterError()
    data class ServerError(override val message: String? = "Server Side Error") : RegisterError()
    data class NetworkError(override val message: String? = "Network Connection Error") : RegisterError()
    data class UsernameOrEmailAlreadyExist(override val message: String? = "Username o email già registrati") : RegisterError()

    data class ValidationError(
        val field: RegisterField,
        val reason: ValidationReason
    ) : RegisterError()

    data class Unknown(override val message: String?) : RegisterError()
}

/**
 * Definisce i campi soggetti a validazione nel dominio Register.
 */
enum class RegisterField {
    EMAIL,
    PASSWORD,
    CONFIRM_PASSWORD
}

/**
 * Definisce le motivazioni tecniche del fallimento di una validazione.
 */
enum class ValidationReason {
    EMPTY,
    INVALID_FORMAT,
    TOO_SHORT,
    NOT_MATCH
}