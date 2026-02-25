package com.example.sharedsocial_kmp.ui.features.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.domain.usecase.LoginUseCase
import com.example.sharedsocial_kmp.navigation.AppNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Gestore della logica di presentazione per la schermata di Login.
 * Coordina lo stato della UI e reagisce agli eventi utente, delegando la logica
 * di business al [LoginUseCase] e la navigazione ad [AppNavigator].
 */
class LoginViewModel(
    private val navigator: AppNavigator,
    private val loginUseCase: LoginUseCase,
    private val dispatchers: AppDispatchers
) : ScreenModel {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    /**
     * Punto di ingresso unico per tutte le azioni provenienti dalla UI.
     * Smista l'evento e aggiorna lo stato o innesca logiche di business.
     */
    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> {
                _state.update { it.copy(email = event.value, emailError = null) }
            }
            is LoginEvent.OnPasswordChanged -> {
                _state.update { it.copy(password = event.value) }
            }
            LoginEvent.OnLoginClicked -> {
                if (state.value.isLoading) return
                performLogin()
            }
        }
    }

    /**
     * Coordina il processo di autenticazione chiamando il UseCase dedicato.
     * Gestisce il passaggio allo stato di caricamento e la navigazione in caso di successo.
     */
    private fun performLogin() {
        _state.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }

        screenModelScope.launch(dispatchers.main) {
            try {
                val result = loginUseCase(state.value.email, state.value.password)

                result.onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                    navigator.navigateToHome()
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Errore imprevisto",
                            isSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = "Errore di rete", isSuccess = false)
                }
            }
        }
    }
}