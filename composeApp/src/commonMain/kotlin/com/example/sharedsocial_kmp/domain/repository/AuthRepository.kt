package com.example.sharedsocial_kmp.domain.repository

import com.example.sharedsocial_kmp.domain.model.User

/**
 * Interfaccia di dominio per la gestione dell'autenticazione.
 * Definisce i contratti per l'accesso ai dati utente e la gestione della sessione,
 * astraendo l'origine dei dati (rete o persistenza locale).
 */
interface AuthRepository {

    /**
     * Tenta l'autenticazione dell'utente tramite credenziali.
     * Restituisce un Result contenente l'utente o l'eccezione verificatasi.
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Termina la sessione utente corrente e rimuove i dati sensibili salvati.
     */
    suspend fun logout(): Result<Unit>

    /**
     * Recupera le informazioni dell'utente attualmente loggato dalla sessione attiva.
     * Restituisce null se l'utente non è autenticato.
     */
    suspend fun getCurrentUser(): User?
}