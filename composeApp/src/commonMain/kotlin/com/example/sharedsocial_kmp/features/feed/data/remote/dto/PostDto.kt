package com.example.sharedsocial_kmp.features.feed.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: Long,
    val testo: String? = null,
    val dataPubblicazione: String,
    val autore: UserDto,
    val numeroCommenti: Int,
    val numeroLikes: Int,
    val mediaUrl: String? = null,
    val mediaType: String? = null,
    val mediaContentType: String? = null,
    val liked: Boolean,
    val mine: Boolean
)