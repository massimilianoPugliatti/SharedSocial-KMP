package com.example.sharedsocial_kmp.ui.features.login

import com.example.sharedsocial_kmp.domain.model.AuthError
import com.example.sharedsocial_kmp.domain.model.AuthField
import com.example.sharedsocial_kmp.domain.model.ValidationReason

/**
 * Mapper dedicato alla trasformazione degli errori di dominio in
 * messaggi leggibili dall'utente (Presentation Logic).
 */
object LoginErrorMapper {

    fun mapToMessage(error: Throwable): String = when (error) {
        is AuthError.InvalidCredentials -> "Credenziali non valide"
        is AuthError.NetworkError -> "Problema di connessione"
        is AuthError.ServerError -> "Server non disponibile"
        is AuthError.ValidationError -> mapValidationReason(error.field, error.reason)
        else -> "Errore imprevisto"
    }

    fun mapValidationReason(field: AuthField, reason: ValidationReason): String {
        return when (field) {
            AuthField.EMAIL -> when (reason) {
                ValidationReason.EMPTY -> "Email obbligatoria"
                ValidationReason.INVALID_FORMAT, ValidationReason.TOO_SHORT -> "Formato email non valido"
            }

            AuthField.PASSWORD -> when (reason) {
                ValidationReason.EMPTY -> "Password obbligatoria"
                ValidationReason.TOO_SHORT -> "Password troppo corta"
                ValidationReason.INVALID_FORMAT -> "Formato password non valido"
            }
        }
    }
}