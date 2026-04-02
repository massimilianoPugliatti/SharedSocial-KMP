package com.example.sharedsocial_kmp.features.feed.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import com.example.sharedsocial_kmp.features.feed.domain.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListSection(
    posts: List<Post>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    renderMedia: @Composable (MediaAsset, Modifier) -> Unit,
    onRefresh: () -> Unit,
    onLikeClick: (Long) -> Unit,
    onCommentClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        when {
            isLoading && posts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            posts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nessun post disponibile")
                }
            }

            else -> {
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { posts.size }
                )

                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val post = posts[page]
                    PostCard(
                        post = post,
                        renderMedia = renderMedia,
                        onLikeClick = { onLikeClick(post.id) },
                        onCommentClick = { onCommentClick(post.id) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}