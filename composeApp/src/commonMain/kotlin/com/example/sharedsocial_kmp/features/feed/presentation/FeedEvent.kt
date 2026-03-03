package com.example.sharedsocial_kmp.features.feed.presentation


sealed interface FeedEvent {

    data class OnNewPostContentChanged(val value: String) : FeedEvent

    data object OnNewPostButtonClicked : FeedEvent

    data class OnPostLikeCliked(val id: Long) : FeedEvent

    data class OnPostCommentCliked(val id: Long) : FeedEvent

    data object OnErrorConsumed: FeedEvent

    data object OnRefreshTriggered: FeedEvent





}