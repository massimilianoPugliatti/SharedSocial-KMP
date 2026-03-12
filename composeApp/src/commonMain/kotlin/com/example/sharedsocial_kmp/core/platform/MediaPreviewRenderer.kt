package com.example.sharedsocial_kmp.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

interface MediaPreviewRenderer {
    @Composable
    fun Render(
        media: MediaAsset,
        modifier: Modifier = Modifier,
    )
}