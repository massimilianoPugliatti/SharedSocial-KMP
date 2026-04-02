package com.example.sharedsocial_kmp.features.feed.domain.model

import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Long,
    val content: String?,
    val media: MediaAsset?,
    val date: String,
    val author: Author,
    val commentsCount: Int,
    val likesCount: Int,
    val liked: Boolean,
    val isMine: Boolean
)