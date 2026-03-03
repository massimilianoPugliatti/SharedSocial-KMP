package com.example.sharedsocial_kmp.features.login.data.repository

import com.example.sharedsocial_kmp.base.BaseTest
import com.example.sharedsocial_kmp.features.auth.data.local.AuthPersistence
import com.example.sharedsocial_kmp.features.auth.data.remote.dto.UserInfoDto
import com.example.sharedsocial_kmp.features.auth.domain.model.AuthError
import com.example.sharedsocial_kmp.features.auth.domain.model.User
import com.example.sharedsocial_kmp.features.auth.data.repository.KtorAuthRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
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
        val userDto = UserInfoDto(
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
        val user = result.getOrThrow()
        assertEquals("Mario Rossi", user.fullName)

        verifySuspend(exactly(1)) { authPersistence.saveToken(mockToken) }
        verifySuspend(exactly(1)) { authPersistence.saveUser(user) }
    }

    /**
     * Verifica che il login fallisca nel caso in cui il server restituisca HTTP 200 OK
     * ma l'header Authorization sia presente ma vuoto.
     * Garantisce che l'eccezione venga mappata in [AuthError.Unknown] e che nessuna
     * operazione di persistenza venga effettuata.
     */
    @Test
    fun `login response with empty token should return failure`() = runTest {
        val email = "test@example.com"
        val userDto = UserInfoDto(
            id = 1,
            username = "testuser",
            nome = "Mario",
            cognome = "Rossi",
            email = email
        )

        val httpClient = createMockHttpClient {
            respond(
                content = Json.encodeToString(userDto),
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.Authorization to listOf(""),
                    HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString())
                )
            )
        }

        val repository = KtorAuthRepository(httpClient, authPersistence, appDispatchers)

        everySuspend { authPersistence.saveToken(any()) } returns Unit
        everySuspend { authPersistence.saveUser(any()) } returns Unit

        val result = repository.login(email, "password123")

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()

        assertTrue(error is AuthError.Unknown)
        assertEquals("Token di autenticazione mancante nella risposta del server", error.message)

        verifySuspend(exactly(0)) { authPersistence.saveToken(any()) }
        verifySuspend(exactly(0)) { authPersistence.saveUser(any()) }
    }

    /**
     * Verifica che il repository restituisca un fallimento quando il server
     * risponde con un codice di errore 401.
     */
    @Test
    fun `login failure with 401 should return invalid api key`() = runTest(appDispatchers.testDispatcher) {
        val httpClient = createMockHttpClient {
            respond(content = "Unauthorized", status = HttpStatusCode.Unauthorized)
        }

        val repository = KtorAuthRepository(httpClient, authPersistence, appDispatchers)
        val result = repository.login("wrong@test.com", "wrongpsw")

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception,"l'eccezione non dovrebbe essere null")
        assertTrue (exception is AuthError.InvalidApiKey, exception.message)
        verifySuspend(exactly(0)) { authPersistence.saveToken(any()) }
        verifySuspend(exactly(0)) { authPersistence.saveUser(any()) }
    }

    /**
     * Verifica il recupero dell'utente corrente dalla memoria locale.
     */
    @Test
    fun `getCurrentUser should return user from persistence`() =
        runTest(appDispatchers.testDispatcher) {
            val repository = KtorAuthRepository(HttpClient(), authPersistence, appDispatchers)
            val expectedUser = User(1, "Mario Rossi", "test@test.it")

            everySuspend { authPersistence.getUser() } returns expectedUser

            val result = repository.getCurrentUser()

            assertEquals(expectedUser, result)
            verifySuspend(exactly(1)) { authPersistence.getUser() }
        }

    /**
     * Verifica la gestione delle eccezioni di rete durante il tentativo di login.
     */
    @Test
    fun `login should return failure when network timeouts`() =
        runTest(appDispatchers.testDispatcher) {
            val httpClient = createMockHttpClient {
                throw SocketTimeoutException("Timeout!", null)
            }

            val repository = KtorAuthRepository(httpClient, authPersistence, appDispatchers)
            val result = repository.login("test@test.com", "password")

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AuthError.NetworkError)
            verifySuspend(exactly(0)) { authPersistence.saveToken(any()) }
            verifySuspend(exactly(0)) { authPersistence.saveUser(any()) }
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
        verifySuspend(exactly(1)) { authPersistence.clear() }
    }
}