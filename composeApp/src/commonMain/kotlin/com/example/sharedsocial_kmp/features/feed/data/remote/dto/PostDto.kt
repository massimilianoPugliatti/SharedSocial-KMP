package com.example.sharedsocial_kmp.features.feed.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: Long,
    val testo: String,
    val dataPubblicazione: String,
    val autore: UserDto,
    val numeroCommenti: Int,
    val numeroLikes: Int,
    val liked: Boolean,
    val mine: Boolean
)