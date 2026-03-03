package com.example.sharedsocial_kmp.features.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseTokenRequest(
    val firebaseToken: String
)