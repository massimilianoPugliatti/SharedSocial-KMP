package com.example.sharedsocial_kmp.features.feed.data.repository

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.features.feed.data.mapper.FeedErrorMapper
import com.example.sharedsocial_kmp.features.feed.data.mapper.toDomain
import com.example.sharedsocial_kmp.features.feed.data.remote.dto.CreatePostRequest
import com.example.sharedsocial_kmp.features.feed.data.remote.dto.LastPostResponse
import com.example.sharedsocial_kmp.features.feed.data.remote.dto.ToggleLikeRequest
import com.example.sharedsocial_kmp.features.feed.domain.model.Post
import com.example.sharedsocial_kmp.features.feed.domain.repository.FeedRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.withContext

/**
 * Implementazione di [FeedRepository] basata sul client Ktor.
 */
class KtorFeedRepository(
    private val httpClient: HttpClient,
    private val dispatchers: AppDispatchers
) : FeedRepository {

    override suspend fun getLatestPosts(): Result<List<Post>> = withContext(dispatchers.io){
        runCatching {
            val response = httpClient.get("Post/getLastPost")

            if (response.status != HttpStatusCode.OK) {
                throw FeedErrorMapper.mapStatusToError(response.status)
            }
            val body = response.body<LastPostResponse>()

            body.postList.map { it.toDomain() }
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(FeedErrorMapper.mapExceptionToError(it)) }
        )
    }

    override suspend fun toggleLike(idPost: Long): Result<Unit> = withContext(dispatchers.io) {
        runCatching {
            val response = httpClient.post("Like/toggleLike") {
                contentType(ContentType.Application.Json)
                setBody(ToggleLikeRequest(idPost =idPost ))
            }

            if (response.status != HttpStatusCode.OK) {
                throw FeedErrorMapper.mapStatusToError(response.status)
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(FeedErrorMapper.mapExceptionToError(it)) }
        )
    }

    override suspend fun newPost(content: String): Result<Unit> = withContext(dispatchers.io){
        runCatching {
            val response = httpClient.post("Post/createPost") {
                contentType(ContentType.Application.Json)
                setBody(CreatePostRequest(testo = content))
            }

            if (response.status != HttpStatusCode.OK) {
                throw FeedErrorMapper.mapStatusToError(response.status)
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(FeedErrorMapper.mapExceptionToError(it)) }
        )
    }


}