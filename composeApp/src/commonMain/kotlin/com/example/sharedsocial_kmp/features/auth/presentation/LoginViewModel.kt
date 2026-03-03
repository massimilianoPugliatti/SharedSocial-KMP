package com.example.sharedsocial_kmp.features.auth.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.features.auth.domain.model.AuthField
import com.example.sharedsocial_kmp.features.auth.domain.usecase.LoginUseCase
import com.example.sharedsocial_kmp.features.auth.domain.validation.AuthValidator
import com.example.sharedsocial_kmp.features.auth.domain.validation.ValidationResult
import com.example.sharedsocial_kmp.core.navigation.AppNavigator
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

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> updateEmail(event.value)
            is LoginEvent.OnPasswordChanged -> updatePassword(event.value)
            LoginEvent.OnLoginClicked -> performLogin()
        }
    }

    private fun performLogin() {
        if (state.value.isLoading) return
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        screenModelScope.launch(dispatchers.main) {
            loginUseCase(state.value.email, state.value.password)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                    navigator.navigateToHome()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = LoginErrorMapper.mapToMessage(error)
                        )
                    }
                }
        }
    }

    private fun updateEmail(value: String) {
        val validation = AuthValidator.validateEmail(value)
        _state.update {
            it.copy(
                email = value,
                emailError = if (validation is ValidationResult.Invalid) {
                    LoginErrorMapper.mapValidationReason(AuthField.EMAIL, validation.reason)
                } else null,
                errorMessage = null
            )

        }
    }

    private fun updatePassword(value: String) {
        val validation = AuthValidator.validatePassword(value)
        _state.update {
            it.copy(
                password = value,
                passwordError = if (validation is ValidationResult.Invalid) {
                    LoginErrorMapper.mapValidationReason(AuthField.PASSWORD, validation.reason)
                } else null,
                errorMessage = null
            )
        }
    }


}