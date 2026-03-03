package com.example.sharedsocial_kmp.features.feed.domain.usecase

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.features.feed.domain.model.FeedError
import com.example.sharedsocial_kmp.features.feed.domain.repository.FeedRepository
import kotlinx.coroutines.withContext

class NewPostuseCaseImpl(
    private val repository: FeedRepository,
    private val dispatchers: AppDispatchers
) : NewPostUseCase {
    override suspend fun invoke(content: String): Result<Unit> = withContext(dispatchers.io) {
        runCatching {
            repository.newPost(content)
        }.getOrElse {
            Result.failure(FeedError.Unknown(it.message))
        }


    }
}