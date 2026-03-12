package com.example.sharedsocial_kmp.features.camera.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.sharedsocial_kmp.core.platform.CameraPreviewRenderer

@Composable
fun CameraPreviewHost(
    previewRenderer: CameraPreviewRenderer,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(Color.Black)
    ) {
        if (isVisible) {
            previewRenderer.Render(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}