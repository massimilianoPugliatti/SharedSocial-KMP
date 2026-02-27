package com.example.sharedsocial_kmp.data.repository

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.data.local.AuthPersistence
import com.example.sharedsocial_kmp.data.mapper.DomainErrorMapper
import com.example.sharedsocial_kmp.data.mapper.toDomain
import com.example.sharedsocial_kmp.data.remote.dto.LoginRequest
import com.example.sharedsocial_kmp.data.remote.dto.UserDto
import com.example.sharedsocial_kmp.domain.model.AuthError
import com.example.sharedsocial_kmp.domain.model.User
import com.example.sharedsocial_kmp.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.withContext

/**
 * Implementazione di [AuthRepository] basata sul client Ktor.
 * Gestisce l'autenticazione remota e la persistenza dei dati di sessione.
 */
class KtorAuthRepository(
    private val httpClient: HttpClient,
    private val authPersistence: AuthPersistence,
    private val dispatchers: AppDispatchers
) : AuthRepository {

    /**
     * Esegue il login e garantisce la persistenza di utente e token.
     * Restituisce un fallimento se il token di autorizzazione è assente nella risposta.
     */
    override suspend fun login(email: String, password: String): Result<User> = withContext(dispatchers.io) {
        runCatching {
            val response = httpClient.post("Utente/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }

            if (response.status != HttpStatusCode.OK) {
                throw DomainErrorMapper.mapStatusToError(response.status)
            }

            val user = response.body<UserDto>().toDomain()
            val token = response.headers["Authorization"]?.removePrefix("Bearer ")

            if (token.isNullOrBlank()) {
                throw AuthError.Unknown("Token di autenticazione mancante nella risposta del server")
            }

            authPersistence.saveToken(token)
            authPersistence.saveUser(user)
            user
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(DomainErrorMapper.mapExceptionToError(it)) }
        )
    }

    /**
     * Rimuove i dati di sessione dai database locali.
     */
    override suspend fun logout(): Result<Unit> = withContext(dispatchers.io) {
        runCatching {
            authPersistence.clear()
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(DomainErrorMapper.mapExceptionToError(it)) }
        )
    }

    /**
     * Recupera l'ultimo utente autenticato salvato localmente.
     */
    override suspend fun getCurrentUser(): User? = withContext(dispatchers.io) {
        authPersistence.getUser()
    }
}