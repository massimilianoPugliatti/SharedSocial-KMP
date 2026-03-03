package com.example.sharedsocial_kmp.features.feed.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Long,
    val content: String,
    val date: String,
    val author: Author,
    val commentsCount: Int,
    val likesCount: Int,
    val liked: Boolean,
    val isMine: Boolean
)