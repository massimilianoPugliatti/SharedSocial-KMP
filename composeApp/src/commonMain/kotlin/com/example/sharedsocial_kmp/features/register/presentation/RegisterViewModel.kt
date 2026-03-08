package com.example.sharedsocial_kmp.features.register.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.features.register.domain.model.RegisterField
import com.example.sharedsocial_kmp.features.register.domain.usecase.RegisterUseCase
import com.example.sharedsocial_kmp.features.register.domain.validation.RegisterValidator
import com.example.sharedsocial_kmp.features.register.domain.validation.ValidationResult
import com.example.sharedsocial_kmp.core.navigation.AppNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Gestore della logica di presentazione per la schermata di registrazione.
 * Coordina lo stato della UI e reagisce agli eventi utente, delegando la logica
 * di business al [RegisterUseCase] e la navigazione ad [AppNavigator].
 */
class RegisterViewModel(
    private val navigator: AppNavigator,
    private val registerUseCase: RegisterUseCase,
    private val dispatchers: AppDispatchers
) : ScreenModel {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnNameChanged -> updateName(event.value)
            is RegisterEvent.OnSurnameChanged -> updateSurname(event.value)
            is RegisterEvent.OnUsernameChanged -> updateUsername(event.value)
            is RegisterEvent.OnEmailChanged -> updateEmail(event.value)
            is RegisterEvent.OnPasswordChanged -> updatePassword(event.value)
            is RegisterEvent.OnConfirmPasswordChanged -> updateConfirmPassword(event.confirmPasswordValue)
            RegisterEvent.OnRegisterClicked -> performRegister()
        }
    }

    private fun performRegister() {
        if (state.value.isLoading) return
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        screenModelScope.launch(dispatchers.main) {
            registerUseCase(
                state.value.name,
                state.value.surname,
                state.value.username,
                state.value.email,
                state.value.password
            )
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                    navigator.navigateToLogin()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = RegisterErrorUIResolver.mapToMessage(error)
                        )
                    }
                }
        }
    }
    private fun updateName(value: String) {
        _state.update {
            it.copy(
                name = value,
                errorMessage = null
            )
        }
    }
    private fun updateSurname(value: String) {
        _state.update {
            it.copy(
                surname = value,
                errorMessage = null
            )
        }
    }
    private fun updateUsername(value: String) {
        _state.update {
            it.copy(
                username = value,
                errorMessage = null
            )
        }
    }
    private fun updateEmail(value: String) {
        val validation = RegisterValidator.validateEmail(value)
        _state.update {
            it.copy(
                email = value,
                emailError = if (validation is ValidationResult.Invalid) {
                    RegisterErrorUIResolver.mapValidationReason(RegisterField.EMAIL, validation.reason)
                } else null,
                errorMessage = null
            )
        }
    }
    private fun updatePassword(value: String) {
        val validation = RegisterValidator.validatePassword(value)
        _state.update {
            it.copy(
                password = value,
                passwordError = if (validation is ValidationResult.Invalid) {
                    RegisterErrorUIResolver.mapValidationReason(
                        RegisterField.PASSWORD,
                        validation.reason
                    )
                } else null,
                errorMessage = null
            )
        }
    }
    private fun updateConfirmPassword(confirmPassword: String) {
        val validation = RegisterValidator.validateConfirmPassword(_state.value.password,confirmPassword)
        _state.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (validation is ValidationResult.Invalid) {
                    RegisterErrorUIResolver.mapValidationReason(
                        RegisterField.CONFIRM_PASSWORD,
                        validation.reason
                    )
                } else null,
                errorMessage = null
            )
        }
    }
}