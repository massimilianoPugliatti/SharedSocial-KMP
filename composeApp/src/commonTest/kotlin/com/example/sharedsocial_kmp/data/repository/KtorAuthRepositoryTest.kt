package com.example.sharedsocial_kmp.data.repository

import com.example.sharedsocial_kmp.base.BaseTest
import com.example.sharedsocial_kmp.data.local.AuthPersistence
import com.example.sharedsocial_kmp.data.remote.dto.UserDto
import com.example.sharedsocial_kmp.domain.model.User
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpResponseData
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test unitari per [KtorAuthRepository].
 * Utilizza il [MockEngine] di Ktor per simulare le risposte del server e verificare
 * l'integrazione tra chiamate di rete e persistenza locale.
 */
class KtorAuthRepositoryTest : BaseTest() {

    private val authPersistence = mock<AuthPersistence>()

    /**
     * Crea un'istanza di [HttpClient] configurata con un motore fittizio.
     * Permette di intercettare le richieste in uscita e restituire risposte predefinite.
     */
    private fun createMockHttpClient(
        handler: suspend MockRequestHandleScope.(io.ktor.client.request.HttpRequestData) -> HttpResponseData
    ): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler { request -> handler(request) }
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            defaultRequest {
                url("https://api.test.com/")
            }
        }
    }

    /**
     * Verifica che un login con esito positivo salvi correttamente sia il token
     * che i dati dell'utente nella persistenza locale.
     */
    @Test
    fun `login success should save token and user data`() = runTest(appDispatchers.testDispatcher) {
        val email = "test@example.com"
        val mockToken = "mock_jwt_token"
        val userDto = UserDto(
            id = 1,
            username = "testuser",
            nome = "Mario",
            cognome = "Rossi",
            email = email
        )

        val httpClient = createMockHttpClient { request ->
            if (request.url.encodedPath.contains("login") && request.method == HttpMethod.Post) {
                respond(
                    content = Json.encodeToString(userDto),
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.Authorization to listOf("Bearer $mockToken"),
                        HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString())
                    )
                )
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }

        val repository = KtorAuthRepository(httpClient, authPersistence, appDispatchers)
        everySuspend { authPersistence.saveToken(any()) } returns Unit
        everySuspend { authPersistence.saveUser(any()) } returns Unit

        val result = repository.login(email, "password123")

        assertTrue(result.isSuccess, "Il login dovrebbe avere successo")
        val user = result.getOrNull()
        assertEquals("Mario Rossi", user?.fullName)

        verifySuspend { authPersistence.saveToken(mockToken) }
        verifySuspend { authPersistence.saveUser(user!!) }
    }

    /**
     * Verifica che il repository restituisca un fallimento quando il server
     * risponde con un codice di errore 401.
     */
    @Test
    fun `login failure should return failure Result`() = runTest(appDispatchers.testDispatcher) {
        val httpClient = createMockHttpClient {
            respond(content = "Unauthorized", status = HttpStatusCode.Unauthorized)
        }

        val repository = KtorAuthRepository(httpClient, authPersistence, appDispatchers)
        val result = repository.login("wrong@test.com", "wrong")

        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message
        assertTrue(message?.contains("401") == true)
    }

    /**
     * Verifica il recupero dell'utente corrente dalla memoria locale.
     */
    @Test
    fun `getCurrentUser should return user from persistence`() = runTest(appDispatchers.testDispatcher) {
        val httpClient = HttpClient(MockEngine) {
            engine { addHandler { respondError(HttpStatusCode.BadRequest) } }
        }
        val repository = KtorAuthRepository(httpClient, authPersistence, appDispatchers)
        val expectedUser = User("1", "Mario Rossi", "test@test.it")

        everySuspend { authPersistence.getUser() } returns expectedUser

        val result = repository.getCurrentUser()

        assertEquals(expectedUser, result)
    }

    /**
     * Verifica la gestione dei timeout di rete durante il tentativo di login.
     */
    @Test
    fun `login should return failure when network timeouts`() = runTest(appDispatchers.testDispatcher) {
        val httpClient = createMockHttpClient {
            throw io.ktor.client.network.sockets.SocketTimeoutException("Timeout!", null)
        }

        val repository = KtorAuthRepository(httpClient, authPersistence, appDispatchers)
        val result = repository.login("test@test.com", "password")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("troppo a rispondere") == true)
    }

    /**
     * Verifica che l'operazione di logout rimuova correttamente i dati dalla persistenza.
     */
    @Test
    fun `logout should clear persistence`() = runTest(appDispatchers.testDispatcher) {
        everySuspend { authPersistence.clear() } returns Unit
        val repository = KtorAuthRepository(HttpClient(), authPersistence, appDispatchers)

        val result = repository.logout()

        assertTrue(result.isSuccess)
        verifySuspend { authPersistence.clear() }
    }
}