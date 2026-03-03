package com.example.sharedsocial_kmp.features.feed.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val id: Long,
    val username: String
)