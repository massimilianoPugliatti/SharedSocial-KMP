package com.example.sharedsocial_kmp.features.feed.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.sharedsocial_kmp.core.platform.MediaPreviewRenderer
import org.koin.compose.koinInject

class FeedScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<FeedViewModel>()
        val state by viewModel.state.collectAsState()
        val mediaPreviewRenderer: MediaPreviewRenderer = koinInject()

        FeedContent(
            state = state,
            mediaPreviewRenderer = mediaPreviewRenderer,
            onEvent = { event -> viewModel.onEvent(event) }
        )
    }
}