package com.example.sharedsocial_kmp.features.feed.domain.model

sealed class FeedError : Throwable() {
    class BadRequest : FeedError()
    class PostNotFound : FeedError()
    class Unauthorized : FeedError()
    class Forbidden : FeedError()
    class ServerError : FeedError()
    class NetworkError : FeedError()
    data class Unknown(override val message: String?) : FeedError()
}

enum class FeedContext {
    LOADING_FEED, CREATING_POST, TOGGLING_LIKE
}