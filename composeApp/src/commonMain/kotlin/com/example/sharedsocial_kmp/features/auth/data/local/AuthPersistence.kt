package com.example.sharedsocial_kmp.features.auth.data.local

import com.example.sharedsocial_kmp.features.auth.domain.model.User

/**
 * Contratto per la gestione della persistenza dei dati di autenticazione.
 * * Definisce le operazioni necessarie per il ciclo di vita della sessione utente,
 * astraendo l'implementazione tecnologica sottostante (es. DataStore, EncryptedPrefs, Keychain)
 * per permettere il riutilizzo della logica nel codice comune (KMP).
 */
interface AuthPersistence {

    /**
     * Persiste il token JWT di sessione in modo sicuro.
     */
    suspend fun saveToken(token: String)

    /**
     * Recupera il token JWT salvato. Restituisce null se non è presente alcuna sessione attiva.
     */
    suspend fun getToken(): String?

    /**
     * Salva le informazioni anagrafiche dell'utente loggato.
     */
    suspend fun saveUser(user: User)

    /**
     * Recupera i dati dell'utente dal database locale o dalle preferenze.
     */
    suspend fun getUser(): User?

    /**
     * Rimuove tutti i dati relativi alla sessione (Token e Profilo Utente).
     * Da utilizzare tipicamente durante il processo di logout.
     */
    suspend fun clear()

    /**
     * Verifica in modo rapido se esiste una sessione valida persistita.
     * @return true se il token è presente, false altrimenti.
     */
    suspend fun isAuthenticated(): Boolean
}