package com.example.sharedsocial_kmp.features.auth.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Rappresentazione remota dell'utente restituita dalle API.
 * Questo modello viene convertito in User nel modulo domain tramite appositi mapper.
 */
@Serializable
data class UserInfoDto(
    val id: Long,
    val username: String,
    val nome: String,
    val cognome: String,
    val email: String
)