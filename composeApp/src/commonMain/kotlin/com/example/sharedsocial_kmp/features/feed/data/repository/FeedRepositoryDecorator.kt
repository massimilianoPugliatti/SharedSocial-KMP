package com.example.sharedsocial_kmp.features.feed.data.repository

import com.example.sharedsocial_kmp.core.platform.AnalyticsService
import com.example.sharedsocial_kmp.features.feed.domain.model.Post
import com.example.sharedsocial_kmp.features.feed.domain.repository.FeedRepository

/**
 * Decoratore di [FeedRepository] che orchestra tracciamento analytics
 */
class FeedRepositoryDecorator(
    private val delegate: FeedRepository,
    private val analytics: AnalyticsService
) : FeedRepository by delegate {

    override suspend fun getLatestPosts(): Result<List<Post>> {
        return delegate.getLatestPosts().onSuccess {
            analytics.logEvent("get_posts_success")
        }.onFailure { error->
            val errorType = error::class.simpleName ?: "Unknown"
            analytics.logEvent("load_posts_failure", mapOf( "error" to errorType))
            analytics.recordNonFatalException(error)
        }
    }
    override suspend fun toggleLike(idPost: Long): Result<Unit> {
        return delegate.toggleLike(idPost).onSuccess {
            analytics.logEvent("like_success")
        }.onFailure { error->
            val errorType = error::class.simpleName ?: "Unknown"
            analytics.logEvent("like_failure", mapOf("post_id" to idPost.toString(), "error" to errorType))
            analytics.recordNonFatalException(error)
        }
    }

    override suspend fun newPost(content: String): Result<Unit> {
        return delegate.newPost(content).onSuccess {
            analytics.logEvent("new_post_success")

        }.onFailure { error->
            val errorType = error::class.simpleName ?: "Unknown"
            analytics.logEvent("new_post_failure", mapOf("error" to errorType))
            analytics.recordNonFatalException(error)
        }
    }
}