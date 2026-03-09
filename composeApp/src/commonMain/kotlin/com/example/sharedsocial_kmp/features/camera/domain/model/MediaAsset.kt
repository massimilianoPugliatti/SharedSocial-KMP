package com.example.sharedsocial_kmp.features.camera.domain.model

sealed interface MediaAsset {
    val localPath: String
    val mimeType: String

    data class Photo(
        override val localPath: String,
        override val mimeType: String = "image/jpeg"
    ) : MediaAsset

    data class Video(
        override val localPath: String,
        override val mimeType: String = "video/mp4",
        val durationMillis: Long? = null
    ) : MediaAsset
}