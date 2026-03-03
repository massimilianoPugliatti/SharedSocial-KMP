package com.example.sharedsocial_kmp.features.auth.data.mapper

import com.example.sharedsocial_kmp.features.auth.domain.model.AuthError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Gestisce la conversione delle risposte HTTP e delle eccezioni tecniche
 * in errori di dominio tipizzati.
 */
object AuthErrorMapper {

    /**
     * Trasforma gli status code HTTP in [AuthError].
     */
    fun mapStatusToError(status: HttpStatusCode): AuthError {
        return when (status) {
            HttpStatusCode.BadRequest -> AuthError.InvalidCredentials()
            HttpStatusCode.Unauthorized -> AuthError.InvalidApiKey()
            HttpStatusCode.Forbidden -> AuthError.Forbidden()
            HttpStatusCode.InternalServerError, HttpStatusCode.ServiceUnavailable -> AuthError.ServerError()
            else -> AuthError.Unknown("Errore imprevisto: ${status.value}")
        }
    }

    /**
     * Trasforma le eccezioni della libreria di rete o di sistema in [AuthError].
     */
    fun mapExceptionToError(e: Throwable): AuthError {
        if (e is AuthError) return e
        return when (e) {
            is SocketTimeoutException, is ConnectTimeoutException -> AuthError.NetworkError()
            is IOException -> AuthError.NetworkError()
            is SerializationException -> AuthError.Unknown("Errore di parsing dei dati")
            else -> AuthError.Unknown(e.message)
        }
    }
}