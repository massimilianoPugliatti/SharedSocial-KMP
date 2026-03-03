package com.example.sharedsocial_kmp.features.root

import app.cash.turbine.test
import com.example.sharedsocial_kmp.base.BaseTest
import com.example.sharedsocial_kmp.features.auth.domain.usecase.IsUserAuthenticatedUseCase
import com.example.sharedsocial_kmp.core.navigation.AppNavigatorImpl
import com.example.sharedsocial_kmp.core.navigation.NavigationAction
import com.example.sharedsocial_kmp.features.feed.presentation.FeedScreen
import com.example.sharedsocial_kmp.features.auth.presentation.LoginScreen
import com.example.sharedsocial_kmp.root.RootViewModel
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

        everySuspend { isUserAuthenticatedUseCase() } returns true


        viewModel.checkAuth()
        testScheduler.advanceUntilIdle()


        navigator.navigationEvents.test {
            val action = awaitItem()
            assertTrue(action is NavigationAction.ReplaceAll)
            assertTrue(action.screen is FeedScreen)
        }
    }

    /**
     * Verifica che il navigatore indirizzi l'utente alla LoginScreen
     * quando lo UseCase conferma che l'utente non è autenticato.
     */
    @Test
    fun `when user is NOT authenticated should navigate to login`() = runTest {

        everySuspend { isUserAuthenticatedUseCase() } returns false


        viewModel.checkAuth()
        testScheduler.advanceUntilIdle()


        navigator.navigationEvents.test {
            val action = awaitItem()
            assertTrue(action is NavigationAction.ReplaceAll)
            assertTrue(action.screen is LoginScreen)
        }
    }
}