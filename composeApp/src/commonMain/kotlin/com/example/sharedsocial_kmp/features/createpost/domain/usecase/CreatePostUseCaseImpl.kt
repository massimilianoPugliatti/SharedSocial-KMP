package com.example.sharedsocial_kmp.features.createpost.domain.usecase

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.features.createpost.domain.model.CreatePostDraft
import com.example.sharedsocial_kmp.features.createpost.domain.model.CreatePostError
import com.example.sharedsocial_kmp.features.createpost.domain.repository.PostRepository
import kotlinx.coroutines.withContext

class CreatePostUseCaseImpl(
    private val repository: PostRepository,
    private val dispatchers: AppDispatchers,
) : CreatePostUseCase {

    override suspend fun invoke(draft: CreatePostDraft): Result<Unit> = withContext(dispatchers.io) {
        runCatching {
            repository.createPost(
                caption = draft.caption,
                media = draft.media,
            )
        }.getOrElse {
            Result.failure(CreatePostError.Unknown(it.message))
        }
    }
}
