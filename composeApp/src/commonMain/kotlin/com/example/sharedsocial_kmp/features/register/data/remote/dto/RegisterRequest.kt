package com.example.sharedsocial_kmp.features.register.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Oggetto di trasferimento dati (DTO) per la richiesta di registrazione.
 */
@Serializable
data class RegisterRequest(
    val nome: String,
    val cognome: String,
    val username: String,
    val email: String,
    val password: String
)