package com.example.sharedsocial_kmp.features.createpost.domain.usecase

import com.example.sharedsocial_kmp.features.createpost.domain.model.CreatePostDraft

interface CreatePostUseCase {
    suspend operator fun invoke(draft: CreatePostDraft): Result<Unit>
}