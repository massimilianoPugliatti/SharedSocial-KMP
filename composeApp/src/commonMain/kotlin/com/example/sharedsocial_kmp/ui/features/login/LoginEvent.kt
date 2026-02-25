package com.example.sharedsocial_kmp.ui.features.login

/**
 * Rappresenta tutte le possibili interazioni dell'utente nella schermata di Login.
 * Utilizzata come canale di comunicazione tra la UI e il [LoginViewModel]
 * seguendo il pattern Unidirectional Data Flow.
 */
sealed interface LoginEvent {
    /**
     * Notifica il cambiamento del testo nel campo email.
     */
    data class OnEmailChanged(val value: String) : LoginEvent

    /**
     * Notifica il cambiamento del testo nel campo password.
     */
    data class OnPasswordChanged(val value: String) : LoginEvent

    /**
     * Scatena il tentativo di autenticazione con le credenziali attuali.
     */
    data object OnLoginClicked : LoginEvent
}