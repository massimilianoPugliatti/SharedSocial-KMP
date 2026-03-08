package com.example.sharedsocial_kmp.features.register.presentation

import com.example.sharedsocial_kmp.features.register.domain.model.RegisterError
import com.example.sharedsocial_kmp.features.register.domain.model.RegisterField
import com.example.sharedsocial_kmp.features.register.domain.model.ValidationReason

/**
 * Mapper dedicato alla trasformazione degli errori di dominio in
 * messaggi leggibili dall'utente (Presentation Logic).
 */
object RegisterErrorUIResolver {

    fun mapToMessage(error: Throwable): String = when (error) {
        is RegisterError.InvalidRequest -> "Parametri di registrazione non validi. Ricontrolla i campi."
        is RegisterError.NetworkError -> "Problema di connessione"
        is RegisterError.ServerError -> "Server non disponibile"
        is RegisterError.UsernameOrEmailAlreadyExist -> "Username o email già esistente"
        is RegisterError.ValidationError -> mapValidationReason(error.field, error.reason)
        else -> "Errore imprevisto"
    }

    fun mapValidationReason(field: RegisterField, reason: ValidationReason): String {
        return when (field) {
            RegisterField.EMAIL -> when (reason) {
                ValidationReason.EMPTY -> "Email obbligatoria"
                ValidationReason.INVALID_FORMAT -> "Formato email non valido"
                else -> "Errore imprevisto"
            }

            RegisterField.PASSWORD -> when (reason) {
                ValidationReason.EMPTY -> "Password obbligatoria"
                ValidationReason.TOO_SHORT -> "Password troppo corta"
                ValidationReason.INVALID_FORMAT -> "Formato password non valido"
                else -> "Errore imprevisto"
            }

            RegisterField.CONFIRM_PASSWORD -> when (reason) {
                ValidationReason.EMPTY -> "Conferma password obbligatoria"
                ValidationReason.TOO_SHORT -> "Password troppo corta"
                ValidationReason.INVALID_FORMAT -> "Formato password non valido"
                ValidationReason.NOT_MATCH -> "Le password non corrispondono"
            }
        }
    }
}