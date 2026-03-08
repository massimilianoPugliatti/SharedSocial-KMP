package com.example.sharedsocial_kmp.features.register.data.repository

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.features.register.data.mapper.RegisterErrorMapper
import com.example.sharedsocial_kmp.features.register.data.remote.dto.RegisterRequest
import com.example.sharedsocial_kmp.features.register.domain.repository.RegisterRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.withContext

/**
 * Implementazione di [RegisterRepository] basata sul client Ktor.
 * Gestisce la registrazione remota.
 */
class KtorRegisterRepository(
    private val httpClient: HttpClient,
    private val dispatchers: AppDispatchers
) : RegisterRepository {

    /**
     * Esegue la registrazione dell'utente.
     */
    override suspend fun register(
        name: String,
        surname: String,
        username: String,
        email: String,
        password: String
    ): Result<String> = withContext(dispatchers.io) {
        runCatching {
            val response = httpClient.post("Utente/createUtente") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(name, surname, username, email, password))
            }

            if (response.status != HttpStatusCode.OK) {
                throw RegisterErrorMapper.mapStatusToError(response.status)
            }

            val body = response.body<String>()


            body
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(RegisterErrorMapper.mapExceptionToError(it)) }
        )
    }
}