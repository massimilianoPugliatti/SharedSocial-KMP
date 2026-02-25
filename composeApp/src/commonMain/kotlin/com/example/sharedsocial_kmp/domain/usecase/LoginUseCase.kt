package com.example.sharedsocial_kmp.domain.usecase

import com.example.sharedsocial_kmp.domain.model.User

/**
 * Rappresenta la logica di business per l'operazione di login.
 * Astrae la chiamata al repository fornendo un punto d'accesso unico per la UI.
 */
interface LoginUseCase {
    /**
     * Esegue il processo di autenticazione.
     * Restituisce l'utente autenticato o un fallimento in caso di credenziali errate o problemi di rete.
     */
    suspend operator fun invoke(email: String, pass: String): Result<User>
}