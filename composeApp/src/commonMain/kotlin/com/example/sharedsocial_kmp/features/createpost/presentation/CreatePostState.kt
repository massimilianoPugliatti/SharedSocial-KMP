package com.example.sharedsocial_kmp.features.createpost.presentation

import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

data class CreatePostState(
    val caption: String = "",
    val media: MediaAsset,
    val isSubmitting: Boolean = false,
    val message: String? = null,
)