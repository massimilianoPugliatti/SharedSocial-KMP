package com.example.sharedsocial_kmp.features.createpost.domain.repository

import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

interface PostRepository {

    suspend fun createPost(
        caption: String,
        media: MediaAsset?,
    ): Result<Unit>

}