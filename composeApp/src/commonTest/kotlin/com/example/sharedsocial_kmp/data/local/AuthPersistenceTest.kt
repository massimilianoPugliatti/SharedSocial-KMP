package com.example.sharedsocial_kmp.data.local

import com.example.sharedsocial_kmp.base.BaseTest
import com.example.sharedsocial_kmp.domain.model.User
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit Test per la logica di persistenza dell'autenticazione.
 * * Verifica il corretto coordinamento tra [AuthPersistence] e lo storage sicuro,
 * assicurando che la serializzazione dei dati utente e la gestione dei token
 * avvengano correttamente tramite l'astrazione [SecureStorage].
 */
class AuthPersistenceTest : BaseTest() {

    private val secureStorage = mock<SecureStorage>()
    private lateinit var authPersistence: AuthPersistence

    @BeforeTest
    fun setup() {
        authPersistence = AuthPersistenceImpl(secureStorage, appDispatchers)
    }

    /**
     * Verifica che il salvataggio del token deleghi correttamente l'operazione
     * al sistema di storage sicuro con la chiave corretta.
     */
    @Test
    fun `saveToken should call secureStorage`() = runTest {
        val token = "my_secret_token"
        everySuspend { secureStorage.saveString(any(), any()) } returns Unit

        authPersistence.saveToken(token)

        verifySuspend { secureStorage.saveString("jwt_token", token) }
    }

    /**
     * Verifica che il sistema di persistenza recuperi correttamente il token
     * precedentemente salvato nello storage sicuro.
     */
    @Test
    fun `getToken should return value from secureStorage`() = runTest {
        val expectedToken = "stored_token"
        everySuspend { secureStorage.getString("jwt_token") } returns expectedToken

        val result = authPersistence.getToken()

        assertEquals(expectedToken, result)
    }

    /**
     * Valida il processo di serializzazione JSON dell'oggetto [User]
     * prima della scrittura su storage persistente.
     */
    @Test
    fun `saveUser should serialize user to json and save it`() = runTest {
        val user = User(id = "1", fullName = "Mario Rossi", email = "mario@test.it")
        val expectedJson = Json.encodeToString(user)
        everySuspend { secureStorage.saveString(any(), any()) } returns Unit

        authPersistence.saveUser(user)

        verifySuspend { secureStorage.saveString("user_data", expectedJson) }
    }

    /**
     * Verifica la corretta ricostruzione dell'oggetto [User] tramite deserializzazione JSON.
     */
    @Test
    fun `getUser should deserialize json from secureStorage`() = runTest {
        val user = User(id = "1", fullName = "Mario Rossi", email = "mario@test.it")
        val userJson = Json.encodeToString(user)
        everySuspend { secureStorage.getString("user_data") } returns userJson

        val result = authPersistence.getUser()

        assertEquals(user, result)
    }

    /**
     * Test di robustezza: verifica che il sistema gestisca correttamente
     * eventuali dati JSON corrotti o non validi nello storage.
     */
    @Test
    fun `getUser should return null if json is invalid`() = runTest {
        everySuspend { secureStorage.getString("user_data") } returns "invalid_json_content"

        val result = authPersistence.getUser()

        assertNull(result)
    }

    /**
     * Verifica lo stato di autenticazione positivo quando è presente un token valido.
     */
    @Test
    fun `isAuthenticated should return true when token exists`() = runTest {
        everySuspend { secureStorage.getString("jwt_token") } returns "some_token"

        val result = authPersistence.isAuthenticated()

        assertTrue(result)
    }

    /**
     * Verifica lo stato di autenticazione negativo quando il token è assente.
     */
    @Test
    fun `isAuthenticated should return false when token is null`() = runTest {
        everySuspend { secureStorage.getString("jwt_token") } returns null

        val result = authPersistence.isAuthenticated()

        assertFalse(result)
    }

    /**
     * Assicura che la funzione di logout o reset elimini correttamente
     * tutti i dati sensibili delegando l'operazione allo storage.
     */
    @Test
    fun `clear should call clear on secureStorage`() = runTest {
        everySuspend { secureStorage.clear() } returns Unit

        authPersistence.clear()

        verifySuspend { secureStorage.clear() }
    }
}