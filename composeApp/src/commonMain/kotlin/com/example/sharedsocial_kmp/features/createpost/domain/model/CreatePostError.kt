package com.example.sharedsocial_kmp.features.createpost.domain.model

sealed class CreatePostError : Throwable() {
    class BadRequest : CreatePostError()
    class Unauthorized : CreatePostError()
    class Forbidden : CreatePostError()
    class UnsupportedMedia : CreatePostError()
    class MediaReadFailed : CreatePostError()
    class NetworkError : CreatePostError()
    class ServerError : CreatePostError()
    data class Unknown(override val message: String?) : CreatePostError()
}
