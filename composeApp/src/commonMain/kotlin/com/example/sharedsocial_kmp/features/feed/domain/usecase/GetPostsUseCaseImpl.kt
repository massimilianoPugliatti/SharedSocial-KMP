package com.example.sharedsocial_kmp.features.feed.domain.usecase

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.features.feed.domain.model.FeedError
import com.example.sharedsocial_kmp.features.feed.domain.model.Post
import com.example.sharedsocial_kmp.features.feed.domain.repository.FeedRepository
import kotlinx.coroutines.withContext

class GetPostsUseCaseImpl(
    private val repository: FeedRepository,
    private val dispatchers: AppDispatchers,
) : GetPostsUseCase {
    override suspend fun invoke(): Result<List<Post>> =  withContext(dispatchers.io) {
        runCatching {
            repository.getLatestPosts()
        }.getOrElse {
            Result.failure(FeedError.Unknown(it.message))
        }
    }
}
