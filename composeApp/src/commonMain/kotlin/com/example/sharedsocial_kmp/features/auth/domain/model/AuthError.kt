package com.example.sharedsocial_kmp.features.auth.domain.model


sealed class AuthError : Throwable() {
    data class InvalidCredentials(override val message: String? = "Invalid Credentials") : AuthError()
    data class InvalidApiKey(override val message: String? = "Invalid API Key") : AuthError()
    data class Forbidden(override val message: String? = "Forbidden Access") : AuthError()
    data class ServerError(override val message: String? = "Server Side Error") : AuthError()
    data class NetworkError(override val message: String? = "Network Connection Error") : AuthError()

    data class ValidationError(
        val field: AuthField,
        val reason: ValidationReason
    ) : AuthError()

    data class Unknown(override val message: String?) : AuthError()
}

/**
 * Definisce i campi soggetti a validazione nel dominio Auth.
 */
enum class AuthField {
    EMAIL,
    PASSWORD
}

/**
 * Definisce le motivazioni tecniche del fallimento di una validazione.
 */
enum class ValidationReason {
    EMPTY,
    INVALID_FORMAT,
    TOO_SHORT
}