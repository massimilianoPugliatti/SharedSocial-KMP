package com.example.sharedsocial_kmp.ui.features.login

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import app.cash.turbine.test
import com.example.sharedsocial_kmp.base.BaseTest
import com.example.sharedsocial_kmp.features.auth.domain.model.User
import com.example.sharedsocial_kmp.features.auth.domain.usecase.LoginUseCase
import com.example.sharedsocial_kmp.core.navigation.AppNavigatorImpl
import com.example.sharedsocial_kmp.core.navigation.NavigationAction
import com.example.sharedsocial_kmp.features.auth.presentation.LoginContent
import com.example.sharedsocial_kmp.features.auth.presentation.LoginState
import com.example.sharedsocial_kmp.features.auth.presentation.LoginViewModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test di interfaccia e integrazione per la feature di Login.
 * * Utilizza [RobolectricTestRunner] per l'esecuzione su JVM.
 * * Sfrutta [BaseTest] per la configurazione centralizzata dei dispatcher e del Main thread.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(application = android.app.Application::class)
class LoginUITest : BaseTest() {

    /**
     * Verifica la logica di validazione del pulsante di login in base allo stato dei campi.
     */
    @Test
    fun loginButton_shouldBeEnabled_onlyWhenFieldsAreValid() = runComposeUiTest {
        val testState = mutableStateOf(LoginState(email = "", password = ""))
        setContent {
            LoginContent(
                state = testState.value,
                onEvent = {}
            )
        }

        onNodeWithTag("login_button").assertIsNotEnabled()

        testState.value = LoginState(email = "test@test.it", password = "password123")

        onNodeWithTag("login_button").assertIsEnabled()
    }

    /**
     * Verifica che il loader sia visibile durante la fase di caricamento asincrono.
     */
    @Test
    fun loader_shouldBeVisible_whenStateIsLoading() = runComposeUiTest {
        setContent {
            LoginContent(
                state = LoginState(isLoading = true),
                onEvent = {}
            )
        }

        onNodeWithTag("login_loader").assertExists()
        onNodeWithText("Accedi").assertDoesNotExist()
    }

    /**
     * Test d'integrazione UI-ViewModel-Navigazione.
     * * Valida il flusso completo dal click sul pulsante fino alla navigazione finale.
     */
    @Test
    fun loginIntegration_withMokkeryCorrectSyntax() = runComposeUiTest {
        val realNavigator = AppNavigatorImpl()
        val mockUseCase = mock<LoginUseCase>()

        everySuspend {
            mockUseCase.invoke(any(), any())
        } returns Result.success(User(id = 123, fullName = "Test User", email = "test@test.it"))

        val viewModel = LoginViewModel(
            navigator = realNavigator,
            loginUseCase = mockUseCase,
            dispatchers = appDispatchers
        )

        setContent {
            val state by viewModel.state.collectAsState()
            LoginContent(
                state = state,
                onEvent = viewModel::onEvent
            )
        }

        onNodeWithTag("email_field").performTextInput("mario@rossi.it")
        onNodeWithTag("password_field").performTextInput("password123")
        onNodeWithTag("login_button").performClick()


        waitForIdle()

        realNavigator.navigationEvents.test {
            val action = awaitItem()
            assertTrue(action is NavigationAction.ReplaceAll)
        }
    }
}