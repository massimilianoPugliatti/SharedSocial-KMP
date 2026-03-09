package com.example.sharedsocial_kmp.core.service

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface CameraPreviewRenderer {
    @Composable
    fun Render(modifier: Modifier = Modifier)
}