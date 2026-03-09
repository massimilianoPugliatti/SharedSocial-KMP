package com.example.sharedsocial_kmp.features.camera.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.sharedsocial_kmp.core.service.CameraPreviewRenderer
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraMode
import com.example.sharedsocial_kmp.features.camera.presentation.screen.CameraContent
import org.koin.compose.koinInject

class CameraScreen(
    private val mode: CameraMode
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: CameraViewModel = koinScreenModel()
        val previewRenderer: CameraPreviewRenderer = koinInject()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onEvent(CameraEvent.OnModeChanged(mode))
            viewModel.onEvent(CameraEvent.OnStart)
        }

        CameraContent(
            state = state,
            preview = { modifier -> previewRenderer.Render(modifier) },
            onEvent = viewModel::onEvent
        )
    }
}