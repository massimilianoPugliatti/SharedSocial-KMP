package com.example.sharedsocial_kmp.core.platform

import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

data class MediaAssetPayload(
    val fileName: String,
    val bytes: ByteArray,
    val mimeType: String,
)

interface MediaAssetReader {
    suspend fun read(media: MediaAsset): Result<MediaAssetPayload>
}
