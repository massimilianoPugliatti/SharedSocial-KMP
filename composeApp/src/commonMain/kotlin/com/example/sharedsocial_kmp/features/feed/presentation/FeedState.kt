package com.example.sharedsocial_kmp.features.feed.presentation

import com.example.sharedsocial_kmp.features.feed.domain.model.Post

data class FeedState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
    val newPostText: String = "",
    val isPublishing: Boolean = false,
    val isRefreshing: Boolean = false,
)
