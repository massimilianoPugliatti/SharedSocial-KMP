package com.example.sharedsocial_kmp.features.createpost.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.sharedsocial_kmp.core.platform.MediaPreviewRenderer
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

class CreatePostScreen(
    private val media: MediaAsset,
) : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<CreatePostViewModel>(
            parameters = { parametersOf(media) }
        )
        val mediaPreviewRenderer: MediaPreviewRenderer = koinInject()
        val state by viewModel.state.collectAsState()


        CreatePostContent(
            state = state,
            preview = { modifier ->
                mediaPreviewRenderer.Render(
                    media = state.media,
                    modifier = modifier
                )
            },
            onEvent = viewModel::onEvent
        )
    }
}