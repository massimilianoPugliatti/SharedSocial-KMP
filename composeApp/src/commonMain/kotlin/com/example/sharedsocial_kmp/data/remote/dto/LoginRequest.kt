package com.example.sharedsocial_kmp.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Oggetto di trasferimento dati (DTO) per la richiesta di autenticazione.
 */
@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)