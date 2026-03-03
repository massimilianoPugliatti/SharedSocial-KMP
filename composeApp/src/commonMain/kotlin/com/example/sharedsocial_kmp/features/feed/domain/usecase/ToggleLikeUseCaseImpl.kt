package com.example.sharedsocial_kmp.features.feed.domain.usecase

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.features.feed.domain.model.FeedError
import com.example.sharedsocial_kmp.features.feed.domain.repository.FeedRepository
import kotlinx.coroutines.withContext

/**
 * Implementazione concreta della logica di toggle like.
 */
class ToggleLikeUseCaseImpl(
    private val repository: FeedRepository,
    private val dispatchers: AppDispatchers
) : ToggleLikeUseCase {
    /**
     * Esegue il processo di toggle like.
     * Restituisce un successo o un fallimento.
     */
    override suspend operator fun invoke(idPost: Long): Result<Unit> =
        withContext(dispatchers.io) {
            runCatching {
                repository.toggleLike(idPost)
            }.getOrElse {
                Result.failure(FeedError.Unknown(it.message))
            }
        }
}