package com.example.sharedsocial_kmp.features.createpost.domain.model

import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

data class CreatePostDraft(
    val caption: String,
    val media: MediaAsset,
)