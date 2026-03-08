package com.example.sharedsocial_kmp.features.register.domain.repository

/**
 * Interfaccia di dominio per la gestione della registrazione.
 */
interface RegisterRepository {

    /**
     * Tenta la registrazione dell'utente.
     */
    suspend fun register(
        name: String,
        surname: String,
        username: String,
        email: String,
        password: String
    ): Result<String>

}