package com.example.sharedsocial_kmp.features.login.domain.usecase

import com.example.sharedsocial_kmp.base.BaseTest
import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.features.auth.domain.model.AuthError
import com.example.sharedsocial_kmp.features.auth.domain.model.AuthField
import com.example.sharedsocial_kmp.features.auth.domain.model.User
import com.example.sharedsocial_kmp.features.auth.domain.repository.AuthRepository
import com.example.sharedsocial_kmp.features.auth.domain.usecase.LoginUseCase
import com.example.sharedsocial_kmp.features.auth.domain.usecase.LoginUseCaseImpl

import dev.mokkery.annotations.DelicateMokkeryApi
import dev.mokkery.answering.Answer
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test unitari per [LoginUseCase].
 * Verifica la logica di validazione delle credenziali, la sanificazione dell'input
 * e la corretta interazione con il repository di autenticazione, inclusa la gestione dei thread.
 */
class LoginUseCaseTest : BaseTest() {

    private val repository = mock<AuthRepository>()
    private lateinit var loginUseCase: LoginUseCase

    @BeforeTest
    fun setup() {
        loginUseCase = LoginUseCaseImpl(repository, appDispatchers)
    }

    /**
     * Verifica che il sistema blocchi preventivamente tentativi con email non valida,
     * restituendo un errore di validazione senza interpellare il repository.
     */
    @Test
    fun `should return failure and not call repository when email is invalid`() = runTest {
        val result = loginUseCase("email_invalid", "123456")
        assertTrue(result.isFailure)
        val error = result.exceptionOrNull() as? AuthError.ValidationError
        assertEquals(AuthField.EMAIL, error?.field)
        verifySuspend(exactly(0)) { repository.login(any(), any()) }
    }

    /**
     * Verifica che il sistema blocchi preventivamente tentativi con password non valida,
     * restituendo un errore di validazione senza interpellare il repository.
     */
    @Test
    fun `should return failure and not call repository when password is invalid`() = runTest {
        val result = loginUseCase("test@validmail.it", "123")
        assertTrue(result.isFailure)
        val error = result.exceptionOrNull() as? AuthError.ValidationError
        assertEquals(AuthField.PASSWORD, error?.field)
        verifySuspend(exactly(0)) { repository.login(any(), any()) }
    }

    /**
     * Verifica che l'input venga ripulito (trim) prima di essere inviato al repository
     * e che il risultato di successo venga propagato correttamente.
     */
    @Test
    fun `should sanitize input and propagate repository success`() = runTest {
        val expectedUser =
            User(id = 101, fullName = "Massimiliano Pugliatti", email = "m.pugliatti@email.it")
        everySuspend { repository.login(any(), any()) } returns Result.success(expectedUser)

        val result = loginUseCase("  m.pugliatti@email.it  ", "  123456  ")

        assertTrue(result.isSuccess)
        verifySuspend(exactly(1)) { repository.login("m.pugliatti@email.it", "123456") }
    }

    /**
     * Verifica che gli errori di dominio restituiti dal repository (es. credenziali errate)
     * vengano propagati inalterati al chiamante.
     */
    @Test
    fun `should propagate failure when repository returns domain error`() = runTest {
        val email = "test@email.it"
        val pass = "wrong_pass"
        val domainError = AuthError.InvalidCredentials()

        everySuspend { repository.login(email, pass) } returns Result.failure(domainError)

        val result = loginUseCase(email, pass)

        assertTrue(result.isFailure)
        assertEquals(domainError, result.exceptionOrNull())
    }

    /**
     * Verifica che le eccezioni impreviste lanciate dal repository vengano catturate
     * e mappate in [AuthError.Unknown].
     */
    @OptIn(DelicateMokkeryApi::class)
    @Test
    fun `should propagate failure when repository returns throw exception`() = runTest {
        everySuspend { repository.login(any(), any()) } answers (Answer.BlockSuspend {
            throw RuntimeException("unknown error")
        })

        val result = loginUseCase("test@email.it", "password123")

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull() as? AuthError.Unknown
        assertEquals("unknown error", error?.message)
    }

    /**
     * Verifica che il UseCase esegua effettivamente il cambio di contesto sul dispatcher IO.
     * Utilizza dispatcher locali isolati per non interferire con la configurazione globale.
     */
    @OptIn(DelicateMokkeryApi::class)
    @Test
    fun `verify IO thread switching without affecting other tests`() = runTest {
        val localIo = StandardTestDispatcher(testScheduler, name = "IO_VERIFIER")
        val localMain = StandardTestDispatcher(testScheduler, name = "MAIN_VERIFIER")

        val localDispatchers = object : AppDispatchers {
            override val main = localMain
            override val io = localIo
            override val default = localIo
        }

        val useCaseToVerify = LoginUseCaseImpl(repository, localDispatchers)

        everySuspend { repository.login(any(), any()) } answers (Answer.BlockSuspend {
            val currentDispatcher = coroutineContext[ContinuationInterceptor]
            assertEquals(localIo, currentDispatcher, "Il UseCase deve girare su IO!")
            Result.success(User(1, "Max", "test@test.it"))
        })

        useCaseToVerify("test@test.it", "password123")
    }
}