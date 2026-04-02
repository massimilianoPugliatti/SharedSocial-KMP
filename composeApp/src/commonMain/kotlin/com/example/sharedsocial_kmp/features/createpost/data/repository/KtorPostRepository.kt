package com.example.sharedsocial_kmp.features.createpost.data.repository

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.core.platform.MediaAssetReader
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import com.example.sharedsocial_kmp.features.createpost.data.mapper.CreatePostErrorMapper
import com.example.sharedsocial_kmp.features.createpost.data.remote.dto.CreatePostTextRequest
import com.example.sharedsocial_kmp.features.createpost.domain.model.CreatePostError
import com.example.sharedsocial_kmp.features.createpost.domain.repository.PostRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.withContext

class KtorPostRepository(
    private val httpClient: HttpClient,
    private val mediaAssetReader: MediaAssetReader,
    private val dispatchers: AppDispatchers,
) : PostRepository {

    override suspend fun createPost(
        caption: String,
        media: MediaAsset?,
    ): Result<Unit> = withContext(dispatchers.io) {
        runCatching {
            when {
                media != null -> createMultipartPost(caption, media)
                caption.isNotBlank() -> createTextOnlyPost(caption)
                else -> throw CreatePostError.BadRequest()
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(CreatePostErrorMapper.mapExceptionToError(it)) },
        )
    }

    private suspend fun createTextOnlyPost(caption: String) {
        val response = httpClient.post("Post/createPost") {
            contentType(ContentType.Application.Json)
            setBody(CreatePostTextRequest(testo = caption))
        }

        if (response.status != HttpStatusCode.OK) {
            throw CreatePostErrorMapper.mapStatusToError(response.status)
        }
    }

    private suspend fun createMultipartPost(
        caption: String,
        media: MediaAsset,
    ) {
        val payload = mediaAssetReader.read(media)
            .getOrElse { throw CreatePostError.MediaReadFailed() }

        val response = httpClient.post("Post/createPostWithMedia") {
            headers.remove(HttpHeaders.ContentType)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        if (caption.isNotBlank()) {
                            append("testo", caption)
                        }

                        append(
                            key = "media",
                            value = payload.bytes,
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, payload.mimeType)
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"${payload.fileName}\""
                                )
                            }
                        )
                    }
                )
            )
        }

        if (response.status != HttpStatusCode.OK) {
            throw CreatePostErrorMapper.mapStatusToError(response.status)
        }
    }
}
