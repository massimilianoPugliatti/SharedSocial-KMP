package com.example.sharedsocial_kmp.features.feed.data.mapper

import com.example.sharedsocial_kmp.features.feed.domain.model.FeedError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Gestisce la conversione delle risposte HTTP e delle eccezioni tecniche
 * in errori di dominio tipizzati.
 */
object FeedErrorMapper {
    fun mapStatusToError(status: HttpStatusCode): FeedError {
        return when (status) {
            HttpStatusCode.BadRequest ->  FeedError.BadRequest()
            HttpStatusCode.NotFound -> FeedError.PostNotFound()
            HttpStatusCode.Unauthorized -> FeedError.Unauthorized()
            HttpStatusCode.Forbidden -> FeedError.Forbidden()
            HttpStatusCode.InternalServerError -> FeedError.ServerError()
            else -> FeedError.Unknown("Status: ${status.value}")
        }
    }

    fun mapExceptionToError(e: Throwable): FeedError {
        if (e is FeedError) return e
        return when (e) {
            is SocketTimeoutException, is ConnectTimeoutException, is IOException -> FeedError.NetworkError()
            is SerializationException -> FeedError.Unknown("Parsing error")
            else -> FeedError.Unknown(e.message)
        }
    }
}