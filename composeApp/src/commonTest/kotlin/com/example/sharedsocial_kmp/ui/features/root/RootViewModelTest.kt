package com.example.sharedsocial_kmp.ui.features.root

import app.cash.turbine.test
import com.example.sharedsocial_kmp.base.BaseTest
import com.example.sharedsocial_kmp.domain.usecase.IsUserAuthenticatedUseCase
import com.example.sharedsocial_kmp.navigation.AppNavigatorImpl
import com.example.sharedsocial_kmp.navigation.NavigationAction
import com.example.sharedsocial_kmp.ui.features.home.HomeScreen
import com.example.sharedsocial_kmp.ui.features.login.LoginScreen
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test unitari per [RootViewModel].
 * Verifica la logica di instradamento iniziale dell'applicazione basata
 * sullo stato di autenticazione dell'utente.
 */
class RootViewModelTest : BaseTest() {

    private val isUserAuthenticatedUseCase = mock<IsUserAuthenticatedUseCase>()
    private val navigator = AppNavigatorImpl()
    private lateinit var viewModel: RootViewModel

    @BeforeTest
    fun setup() {
        viewModel = RootViewModel(isUserAuthenticatedUseCase, navigator)
    }

    /**
     * Verifica che il navigatore indirizzi l'utente alla HomeScreen
     * quando il UseCase conferma che l'utente è autenticato.
     */
    @Test
    fun `when user is authenticated should navigate to home`() = runTest {
        // ARRANGE
        everySuspend { isUserAuthenticatedUseCase() } returns true

        // ACT
        viewModel.checkAuth()
        testScheduler.advanceUntilIdle()

        // ASSERT
        navigator.navigationEvents.test {
            val action = awaitItem()
            assertTrue(action is NavigationAction.ReplaceAll)
            assertTrue(action.screen is HomeScreen)
        }
    }

    /**
     * Verifica che il navigatore indirizzi l'utente alla LoginScreen
     * quando lo UseCase conferma che l'utente non è autenticato.
     */
    @Test
    fun `when user is NOT authenticated should navigate to login`() = runTest {
        // ARRANGE
        everySuspend { isUserAuthenticatedUseCase() } returns false

        // ACT
        viewModel.checkAuth()
        testScheduler.advanceUntilIdle()

        // ASSERT
        navigator.navigationEvents.test {
            val action = awaitItem()
            assertTrue(action is NavigationAction.ReplaceAll)
            assertTrue(action.screen is LoginScreen)
        }
    }
}