package com.example.sharedsocial_kmp.features.feed.domain.usecase

interface NewPostUseCase {
    suspend operator fun invoke(content : String): Result<Unit>

}