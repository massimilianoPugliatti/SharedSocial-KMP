package com.example.sharedsocial_kmp.features.createpost.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostTextRequest(
    val testo: String,
)
