package com.example.sharedsocial_kmp.features.feed.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.sharedsocial_kmp.core.platform.MediaPreviewRenderer
import com.example.sharedsocial_kmp.features.feed.presentation.components.PostListSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedContent(
    state: FeedState,
    mediaPreviewRenderer: MediaPreviewRenderer,
    onEvent: (FeedEvent) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            onEvent(FeedEvent.OnErrorConsumed)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Feed", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                )
            )
        }
    ) { padding ->
        PostListSection(
            posts = state.posts,
            isLoading = state.isLoading,
            isRefreshing = state.isRefreshing,
            mediaPreviewRenderer = mediaPreviewRenderer,
            onRefresh = { onEvent(FeedEvent.OnRefreshTriggered) },
            onLikeClick = { id -> onEvent(FeedEvent.OnPostLikeCliked(id)) },
            onCommentClick = { id -> onEvent(FeedEvent.OnPostCommentCliked(id)) },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}