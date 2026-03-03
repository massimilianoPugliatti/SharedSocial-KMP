package com.example.sharedsocial_kmp.features.auth.domain.model

import kotlinx.serialization.Serializable

/**
 * Rappresenta l'utente all'interno del dominio dell'applicazione.
 * Questo modello aggrega i dati provenienti dai DTO per fornire una struttura
 * ottimizzata per la visualizzazione nella UI.
 */
@Serializable
data class User(
    val id: Long,
    val fullName: String,
    val email: String
)