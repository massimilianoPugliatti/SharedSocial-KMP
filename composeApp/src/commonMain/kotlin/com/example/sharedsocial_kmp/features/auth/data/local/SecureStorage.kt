package com.example.sharedsocial_kmp.features.auth.data.local

/**
 * Astrazione per la gestione della persistenza sicura dei dati sensibili.
 * * Questa interfaccia definisce il contratto per lo storage crittografato,
 * permettendo al codice condiviso (common) di interagire con le implementazioni
 * hardware-backed specifiche per Android (Keystore) e iOS (Keychain).
 */
interface SecureStorage {

    /**
     * Cifra e memorizza una stringa in modo persistente.
     */
    suspend fun saveString(key: String, value: String)

    /**
     * Recupera e decifra una stringa salvata. Restituisce null se la chiave
     * non esiste o se la decifrazione fallisce a causa di chiavi invalidate.
     */
    suspend fun getString(key: String): String?

    /**
     * Rimuove permanentemente tutti i dati e le chiavi gestite dallo storage.
     */
    suspend fun clear()
}