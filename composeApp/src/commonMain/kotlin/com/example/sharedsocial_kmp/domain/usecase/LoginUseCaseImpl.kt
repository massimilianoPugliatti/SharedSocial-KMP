package com.example.sharedsocial_kmp.domain.usecase

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.domain.model.AuthError
import com.example.sharedsocial_kmp.domain.model.AuthField
import com.example.sharedsocial_kmp.domain.model.User
import com.example.sharedsocial_kmp.domain.repository.AuthRepository
import com.example.sharedsocial_kmp.domain.validation.AuthValidator
import com.example.sharedsocial_kmp.domain.validation.ValidationResult
import kotlinx.coroutines.withContext

/**
 * Implementazione concreta della logica di login.
 * Include la validazione sintattica dell'input prima di delegare l'autenticazione al repository.
 */
class LoginUseCaseImpl(
    private val repository: AuthRepository, private val dispatchers: AppDispatchers
) : LoginUseCase {

    /**
     * Esegue la validazione dei parametri e avvia la richiesta di login.
     * Gestisce la pulizia delle stringhe (trim) e il cambio di contesto sui thread I/O.
     */
    override suspend operator fun invoke(email: String, pass: String): Result<User> =
        withContext(dispatchers.io) {
            val cleanEmail = email.trim()
            val cleanPass = pass.trim()
            val emailValidation = AuthValidator.validateEmail(cleanEmail)
            if (emailValidation is ValidationResult.Invalid) {
                return@withContext Result.failure(
                    AuthError.ValidationError(
                        AuthField.EMAIL, emailValidation.reason
                    )
                )
            }
            val passwordValidation = AuthValidator.validatePassword(cleanPass)
            if (passwordValidation is ValidationResult.Invalid) {
                return@withContext Result.failure(
                    AuthError.ValidationError(
                        AuthField.PASSWORD, passwordValidation.reason
                    )
                )
            }
            runCatching {
                repository.login(cleanEmail, cleanPass)
            }.getOrElse {
                Result.failure(AuthError.Unknown(it.message))
            }
        }
}