package com.example.sharedsocial_kmp.features.feed.domain.repository

import com.example.sharedsocial_kmp.features.feed.domain.model.Post


interface FeedRepository {

    suspend fun getLatestPosts():  Result<List<Post>>

    suspend fun toggleLike(idPost: Long): Result<Unit>

    suspend fun newPost(content: String): Result<Unit>



}