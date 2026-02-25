package com.example.sharedsocial_kmp.domain.usecase

import com.example.sharedsocial_kmp.base.BaseTest
import com.example.sharedsocial_kmp.domain.model.User
import com.example.sharedsocial_kmp.domain.repository.AuthRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test unitari per [LoginUseCase].
 * Verifica la logica di validazione delle credenziali e la corretta
 * interazione con il repository di autenticazione.
 */
class LoginUseCaseTest : BaseTest() {

    private val repository = mock<AuthRepository>()
    private lateinit var loginUseCase: LoginUseCase

    @BeforeTest
    fun setup() {
        loginUseCase = LoginUseCaseImpl(repository, appDispatchers)
    }

    /**
     * Verifica che il sistema blocchi preventivamente tentativi con campi vuoti,
     * senza interpellare il repository.
     */
    @Test
    fun `should return failure when email or password is blank`() = runTest {
        val result = loginUseCase("", "123456")

        assertTrue(result.isFailure)
        assertEquals("Email e password obbligatorie", result.exceptionOrNull()?.message)

        verifySuspend(exactly(0)) { repository.login(any(), any()) }
    }

    /**
     * Verifica la validazione sintattica dell'email.
     */
    @Test
    fun `should return failure when email format is invalid`() = runTest {
        val result = loginUseCase("utente_senza_at.it", "123456")

        assertTrue(result.isFailure)
        assertEquals("Formato email non valido", result.exceptionOrNull()?.message)

        verifySuspend(exactly(0)) { repository.login(any(), any()) }
    }

    /**
     * Verifica il flusso di successo quando le credenziali superano i controlli
     * locali e il server conferma l'identità.
     */
    @Test
    fun `should return success when credentials are valid and repository succeeds`() = runTest {
        val email = "m.pugliatti@email.it"
        val pass = "password123"
        val expectedUser = User(id = "101", fullName = "Massimiliano Pugliatti", email = email)

        everySuspend { repository.login(email, pass) } returns Result.success(expectedUser)

        val result = loginUseCase(email, pass)

        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
        verifySuspend(exactly(1)) { repository.login(email, pass) }
    }

    /**
     * Verifica che gli errori provenienti dal repository (es. credenziali errate sul server)
     * vengano correttamente propagati al chiamante.
     */
    @Test
    fun `should propagate failure when repository returns error`() = runTest {
        val email = "test@email.it"
        val pass = "wrong_pass"
        val exception = Exception("Credenziali errate")

        everySuspend { repository.login(email, pass) } returns Result.failure(exception)

        val result = loginUseCase(email, pass)

        assertTrue(result.isFailure)
        assertEquals("Credenziali errate", result.exceptionOrNull()?.message)
        verifySuspend(exactly(1)) { repository.login(email, pass) }
    }

    /**
     * Verifica la resilienza del sistema a piccoli errori di inserimento (spazi vuoti),
     * assicurando che l'input venga normalizzato (trimmed) prima del login.
     */
    @Test
    fun `should succeed even if email has trailing spaces`() = runTest {
        val email = "test@test.it "
        val pass = "password"
        everySuspend { repository.login("test@test.it", pass) } returns Result.success(
            User("1", "Max", "test@test.it")
        )

        val result = loginUseCase(email, pass)
        assertTrue(result.isSuccess)
    }
}