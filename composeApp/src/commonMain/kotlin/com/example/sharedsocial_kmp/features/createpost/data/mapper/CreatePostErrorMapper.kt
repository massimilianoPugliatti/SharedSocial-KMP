package com.example.sharedsocial_kmp.features.createpost.data.mapper

import com.example.sharedsocial_kmp.features.createpost.domain.model.CreatePostError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

object CreatePostErrorMapper {
    fun mapStatusToError(status: HttpStatusCode): CreatePostError {
        return when (status) {
            HttpStatusCode.BadRequest -> CreatePostError.BadRequest()
            HttpStatusCode.Unauthorized -> CreatePostError.Unauthorized()
            HttpStatusCode.Forbidden -> CreatePostError.Forbidden()
            HttpStatusCode.InternalServerError -> CreatePostError.ServerError()
            else -> CreatePostError.Unknown("Status: ${status.value}")
        }
    }

    fun mapExceptionToError(error: Throwable): CreatePostError {
        if (error is CreatePostError) return error

        return when (error) {
            is SocketTimeoutException,
            is ConnectTimeoutException,
            is IOException -> CreatePostError.NetworkError()

            is SerializationException -> CreatePostError.Unknown("Parsing error")
            else -> CreatePostError.Unknown(error.message)
        }
    }
}
