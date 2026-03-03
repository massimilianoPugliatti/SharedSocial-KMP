package com.example.sharedsocial_kmp.root

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sharedsocial_kmp.features.auth.data.local.AuthPersistence
import com.example.sharedsocial_kmp.features.auth.domain.usecase.IsUserAuthenticatedUseCase
import com.example.sharedsocial_kmp.core.navigation.AppNavigator
import kotlinx.coroutines.launch

/**
 * Orchestratore del flusso di avvio dell'applicazione.
 * Verifica la presenza di una sessione attiva tramite [AuthPersistence] e instrada
 * l'utente verso la destinazione appropriata (Home o Login).
 */
class RootViewModel(
    private val isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase,
    private val navigator: AppNavigator
) : ScreenModel {

    /**
     * Analizza lo stato di autenticazione in modo asincrono.
     * Innesca un evento di navigazione immediato per rimuovere la RootScreen dallo stack.
     */
    fun checkAuth() {
        screenModelScope.launch {
            if (isUserAuthenticatedUseCase()) {
                navigator.navigateToHome()
            } else {
                navigator.navigateToLogin()
            }
        }
    }
}