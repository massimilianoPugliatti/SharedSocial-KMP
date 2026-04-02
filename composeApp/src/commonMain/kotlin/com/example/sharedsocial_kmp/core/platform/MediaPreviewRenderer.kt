package com.example.sharedsocial_kmp.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

enum class MediaRenderScaleMode {
    Fit,
    Fill
}

data class VideoPlaybackUiState(
    val isLoading: Boolean = false,
    val canReplay: Boolean = false,
)

interface MediaPreviewRenderer {
    @Composable
    fun Render(
        media: MediaAsset,
        modifier: Modifier = Modifier,
        isActive: Boolean = true,
        replayToken: Int = 0,
        scaleMode: MediaRenderScaleMode = MediaRenderScaleMode.Fit,
        onVideoUiStateChanged: (VideoPlaybackUiState) -> Unit = {},
    )
}