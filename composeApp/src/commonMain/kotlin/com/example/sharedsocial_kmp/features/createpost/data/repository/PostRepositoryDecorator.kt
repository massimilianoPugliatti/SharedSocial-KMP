package com.example.sharedsocial_kmp.features.createpost.data.repository

import com.example.sharedsocial_kmp.core.platform.AnalyticsService
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import com.example.sharedsocial_kmp.features.createpost.domain.repository.PostRepository

class PostRepositoryDecorator(
    private val delegate: PostRepository,
    private val analytics: AnalyticsService,
) : PostRepository by delegate {

    override suspend fun createPost(
        caption: String,
        media: MediaAsset?,
    ): Result<Unit> {
        return delegate.createPost(caption, media).onSuccess {
            analytics.logEvent(
                "create_post_success",
                mapOf(
                    "has_caption" to caption.isNotBlank().toString(),
                    "media_type" to media?.mimeType.orEmpty(),
                )
            )
        }.onFailure { error ->
            analytics.recordNonFatalException(error)
            analytics.logEvent(
                "create_post_failure",
                mapOf(
                    "error" to (error::class.simpleName ?: "Unknown"),
                    "media_type" to media?.mimeType.orEmpty(),
                )
            )
        }
    }
}
