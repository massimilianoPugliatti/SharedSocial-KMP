package com.example.sharedsocial_kmp.features.feed.domain.usecase

import com.example.sharedsocial_kmp.features.feed.domain.model.Post

interface GetPostsUseCase {
    suspend operator fun invoke(): Result<List<Post>>

}
