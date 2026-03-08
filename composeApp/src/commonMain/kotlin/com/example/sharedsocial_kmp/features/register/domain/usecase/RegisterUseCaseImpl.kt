package com.example.sharedsocial_kmp.features.register.domain.usecase

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.features.register.domain.model.RegisterError
import com.example.sharedsocial_kmp.features.register.domain.model.RegisterField
import com.example.sharedsocial_kmp.features.register.domain.repository.RegisterRepository
import com.example.sharedsocial_kmp.features.register.domain.validation.RegisterValidator
import com.example.sharedsocial_kmp.features.register.domain.validation.ValidationResult
import kotlinx.coroutines.withContext

/**
 * Implementazione concreta della logica di registrazione.
 * Include la validazione sintattica dell'input prima di delegare la registrazione al repository.
 */
class RegisterUseCaseImpl(
    private val repository: RegisterRepository, private val dispatchers: AppDispatchers
) : RegisterUseCase {

    /**
     * Esegue la validazione dei parametri e avvia la richiesta di registrazione.
     * Gestisce la pulizia delle stringhe (trim) e il cambio di contesto sui thread I/O.
     */
    override suspend operator fun invoke(
        name: String,
        surname: String,
        username: String,
        email: String,
        pass: String
    ): Result<String> = withContext(dispatchers.io) {
        val cleanEmail = email.trim()
        val cleanPass = pass.trim()
        val cleanUsername = username.trim()
        val cleanName = name.trim()
        val cleanSurname = surname.trim()
        val emailValidation = RegisterValidator.validateEmail(cleanEmail)
        if (emailValidation is ValidationResult.Invalid) {
            return@withContext Result.failure(
                RegisterError.ValidationError(
                    RegisterField.EMAIL, emailValidation.reason
                )
            )
        }
        val passwordValidation = RegisterValidator.validatePassword(cleanPass)
        if (passwordValidation is ValidationResult.Invalid) {
            return@withContext Result.failure(
                RegisterError.ValidationError(
                    RegisterField.PASSWORD, passwordValidation.reason
                )
            )
        }
        repository.register(cleanUsername, cleanName, cleanSurname, cleanEmail, cleanPass)
    }
}