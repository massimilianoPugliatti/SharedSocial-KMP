package com.example.sharedsocial_kmp.domain.usecase

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.domain.model.User
import com.example.sharedsocial_kmp.domain.repository.AuthRepository
import kotlinx.coroutines.withContext

/**
 * Implementazione concreta della logica di login.
 * Include la validazione sintattica dell'input prima di delegare l'autenticazione al repository.
 */
class LoginUseCaseImpl(
    private val repository: AuthRepository,
    private val dispatchers: AppDispatchers
): LoginUseCase {

    /**
     * Esegue la validazione dei parametri e avvia la richiesta di login.
     * Gestisce la pulizia delle stringhe (trim) e il cambio di contesto sui thread I/O.
     */
    override suspend operator fun invoke(email: String, pass: String): Result<User> = withContext(dispatchers.io) {
        val cleanEmail = email.trim()
        val cleanPass = pass.trim()

        if (cleanEmail.isBlank() || cleanPass.isBlank()) {
            Result.failure(Exception("Email e password obbligatorie"))
        } else if (!cleanEmail.contains("@")) {
            Result.failure(Exception("Formato email non valido"))
        } else {
            repository.login(cleanEmail, cleanPass)
        }
    }
}