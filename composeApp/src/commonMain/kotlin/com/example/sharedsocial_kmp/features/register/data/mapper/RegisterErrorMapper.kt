package com.example.sharedsocial_kmp.features.register.data.mapper

import com.example.sharedsocial_kmp.features.register.domain.model.RegisterError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Gestisce la conversione delle risposte HTTP e delle eccezioni tecniche
 * in errori di dominio tipizzati.
 */
object RegisterErrorMapper {

    /**
     * Trasforma gli status code HTTP in [RegisterError].
     */
    fun mapStatusToError(status: HttpStatusCode): RegisterError {
        return when (status) {
            HttpStatusCode.BadRequest -> RegisterError.InvalidRequest()
            HttpStatusCode.Unauthorized -> RegisterError.InvalidApiKey()
            HttpStatusCode.Forbidden -> RegisterError.Forbidden()
            HttpStatusCode.InternalServerError, HttpStatusCode.ServiceUnavailable -> RegisterError.ServerError()
            HttpStatusCode.Conflict -> RegisterError.UsernameOrEmailAlreadyExist()
            else -> RegisterError.Unknown("Errore imprevisto: ${status.value}")
        }
    }

    /**
     * Trasforma le eccezioni della libreria di rete o di sistema in [RegisterError].
     */
    fun mapExceptionToError(e: Throwable): RegisterError {
        if (e is RegisterError) return e
        return when (e) {
            is SocketTimeoutException, is ConnectTimeoutException -> RegisterError.NetworkError()
            is IOException -> RegisterError.NetworkError()
            is SerializationException -> RegisterError.Unknown("Errore di parsing dei dati")
            else -> RegisterError.Unknown(e.message)
        }
    }
}