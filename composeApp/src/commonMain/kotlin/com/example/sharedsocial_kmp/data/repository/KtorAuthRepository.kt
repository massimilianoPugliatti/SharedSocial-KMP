package com.example.sharedsocial_kmp.data.repository

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.data.local.AuthPersistence
import com.example.sharedsocial_kmp.data.remote.dto.UserDto
import com.example.sharedsocial_kmp.data.mapper.toDomain
import com.example.sharedsocial_kmp.data.remote.dto.LoginRequest
import com.example.sharedsocial_kmp.domain.model.User
import com.example.sharedsocial_kmp.domain.repository.AuthRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.withContext

/**
 * Implementazione di [AuthRepository] basata sul client Ktor.
 * Gestisce il flusso di autenticazione integrando le chiamate API con la
 * persistenza locale dei token e dei dati utente.
 */
class KtorAuthRepository(
    private val httpClient: HttpClient,
    private val authPersistence: AuthPersistence,
    private val dispatchers: AppDispatchers
) : AuthRepository {

    /**
     * Esegue il login, estrae il token JWT dagli header e persiste il profilo utente.
     */
    override suspend fun login(email: String, password: String): Result<User> = withContext(
        dispatchers.io
    ) {
        val requestBody = LoginRequest(email, password)
        try {
            val response = httpClient.post("Utente/login") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status == HttpStatusCode.OK) {
                val token = response.headers["Authorization"]?.removePrefix("Bearer ")
                token?.let { authPersistence.saveToken(it) }

                val userDto = response.body<UserDto>()
                val user = userDto.toDomain()
                authPersistence.saveUser(user)

                Result.success(user)
            } else {
                Result.failure(Exception("Errore Server: ${response.status.value}"))
            }
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Il server ci sta mettendo troppo a rispondere. Riprova più tardi."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Rimuove la sessione corrente pulendo lo storage sicuro.
     */
    override suspend fun logout(): Result<Unit> {
        authPersistence.clear()
        return Result.success(Unit)
    }

    /**
     * Recupera l'utente attualmente loggato dalla cache locale.
     */
    override suspend fun getCurrentUser(): User? {
        return authPersistence.getUser()
    }
}