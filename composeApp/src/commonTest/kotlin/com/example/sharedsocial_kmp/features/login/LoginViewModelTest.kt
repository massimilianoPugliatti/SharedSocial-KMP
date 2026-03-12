package com.example.sharedsocial_kmp.features.login

import app.cash.turbine.test
import com.example.sharedsocial_kmp.base.BaseTest
import com.example.sharedsocial_kmp.features.auth.domain.model.AuthError
import com.example.sharedsocial_kmp.features.auth.domain.model.User
import com.example.sharedsocial_kmp.core.navigation.NavigationAction
import com.example.sharedsocial_kmp.core.navigation.AppNavigatorImpl
import com.example.sharedsocial_kmp.features.auth.domain.usecase.LoginUseCase
import com.example.sharedsocial_kmp.features.auth.presentation.AuthErrorUIResolver
import com.example.sharedsocial_kmp.features.auth.presentation.LoginEvent
import com.example.sharedsocial_kmp.features.auth.presentation.LoginState
import com.example.sharedsocial_kmp.features.auth.presentation.LoginViewModel
import com.example.sharedsocial_kmp.features.feed.presentation.FeedScreen
import com.example.sharedsocial_kmp.features.home.presentation.HomePagerScreen
import dev.mokkery.MokkeryBlockingCallScope
import dev.mokkery.MokkerySuspendCallScope
import dev.mokkery.annotations.DelicateMokkeryApi
import dev.mokkery.answering.Answer
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Suite di test per [LoginViewModel].
 * Verifica la gestione reattiva dello stato e il coordinamento tra il business layer (UseCase)
 * e la navigazione dell'interfaccia utente.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : BaseTest() {

    private val navigator = AppNavigatorImpl()
    private val loginUseCase = mock<LoginUseCase>()
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setup() {
        viewModel = LoginViewModel(navigator, loginUseCase, appDispatchers)
    }

    /**
     * Verifica che un login con successo scateni correttamente la navigazione verso la Home.
     */
    @Test
    fun `login success triggers navigation`() = runTest {
        val user = User(1, "Max", "email@input")
        everySuspend { loginUseCase(any(), any()) } returns Result.success(user)

        navigator.navigationEvents.test {
            viewModel.onEvent(LoginEvent.OnLoginClicked)

            val action = awaitItem()
            assertTrue(action is NavigationAction.ReplaceAll)
            assertTrue(action.screen is HomePagerScreen)
        }
    }

    /**
     * Valida l'intera sequenza di cambiamenti dello stato durante il flusso di login,
     * dall'input dell'utente al successo finale.
     */
    @Test
    fun `when login succeeds, verify full state sequence and navigation`() = runTest {
        val emailInput = "test@test.it"
        val pass = "password123"
        val user = User(1, "Max", emailInput)

        everySuspend { loginUseCase(any(), any()) } returns Result.success(user)

        viewModel.state.test {
            assertEquals(LoginState(), awaitItem())

            viewModel.onEvent(LoginEvent.OnEmailChanged(emailInput))
            assertEquals(LoginState(email = emailInput), awaitItem())

            viewModel.onEvent(LoginEvent.OnPasswordChanged(pass))
            assertEquals(LoginState(email = emailInput, password = pass), awaitItem())

            viewModel.onEvent(LoginEvent.OnLoginClicked)
            appDispatchers.testDispatcher.scheduler.runCurrent()

            assertEquals(
                LoginState(
                    email = emailInput,
                    password = pass,
                    isLoading = true,
                    errorMessage = null,
                    isSuccess = false
                ),
                awaitItem()
            )

            appDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                LoginState(
                    email = emailInput,
                    password = pass,
                    isLoading = false,
                    errorMessage = null,
                    isSuccess = true
                ),
                awaitItem()
            )

            navigator.navigationEvents.test {
                val action = awaitItem()
                assertTrue(action is NavigationAction.ReplaceAll)
                assertTrue(action.screen is HomePagerScreen)
            }
        }
    }

    /**
     * Verifica che in caso di errore, lo stato rifletta correttamente il messaggio ricevuto
     * e interrompa lo stato di caricamento.
     */
    @Test
    fun `when login fails, verify error state covers all fields`() = runTest {
        val email = "error@test.it"
        val pass = "wrongpass"
        val expectedErrorMsg = AuthErrorUIResolver.mapToMessage(AuthError.InvalidCredentials())

        everySuspend {
            loginUseCase(
                any(),
                any()
            )
        } returns Result.failure(AuthError.InvalidCredentials())

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(LoginEvent.OnEmailChanged(email))
            assertEquals(email, awaitItem().email)

            viewModel.onEvent(LoginEvent.OnPasswordChanged(pass))
            assertEquals(pass, awaitItem().password)

            viewModel.onEvent(LoginEvent.OnLoginClicked)

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            appDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            val finalState = awaitItem()

            assertEquals(expectedErrorMsg, finalState.errorMessage)
            assertEquals(email, finalState.email)
            assertEquals(pass, finalState.password)
            assertFalse(finalState.isLoading)
            assertFalse(finalState.isSuccess)
        }
    }

    /**
     * Garantisce l'idempotenza del comando di login: se una richiesta è in corso,
     * i click successivi devono essere ignorati per evitare chiamate multiple al server.
     */
    @OptIn(DelicateMokkeryApi::class)
    @Test
    fun `when login is loading, subsequent clicks are ignored`() = runTest {
        everySuspend { loginUseCase(any(), any()) } answers object : Answer<Result<User>> {
            override fun call(scope: MokkeryBlockingCallScope): Result<User> =
                error("Not supported")

            override suspend fun call(scope: MokkerySuspendCallScope): Result<User> {
                delay(1000)
                return Result.success(User(1, "Max", "test@test.it"))
            }
        }

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(LoginEvent.OnLoginClicked)
            assertTrue(awaitItem().isLoading)

            viewModel.onEvent(LoginEvent.OnLoginClicked)
            viewModel.onEvent(LoginEvent.OnLoginClicked)

            appDispatchers.testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(awaitItem().isSuccess)
            verifySuspend(exactly(1)) { loginUseCase(any(), any()) }
        }
    }

    /**
     * Verifica la gestione degli errori di timeout, assicurando che l'utente riceva
     * un feedback chiaro invece di un caricamento infinito.
     */
    @Test
    fun `when repository returns timeout error, state should show specific message`() = runTest {
        val errorMsg = AuthErrorUIResolver.mapToMessage(AuthError.NetworkError())

        everySuspend { loginUseCase(any(), any()) } returns Result.failure(AuthError.NetworkError())

        viewModel.onEvent(LoginEvent.OnEmailChanged("test@test.it"))
        viewModel.onEvent(LoginEvent.OnPasswordChanged("password"))

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(LoginEvent.OnLoginClicked)

            assertTrue(awaitItem().isLoading)

            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals(errorMsg, finalState.errorMessage)
        }
    }
}