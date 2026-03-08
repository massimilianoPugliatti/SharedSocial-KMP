package com.example.sharedsocial_kmp.features.register.presentation

/**
 * Rappresenta tutte le possibili interazioni dell'utente nella schermata di registrazione.
 * Utilizzata come canale di comunicazione tra la UI e il [RegisterViewModel]
 * seguendo il pattern Unidirectional Data Flow.
 */
sealed interface RegisterEvent {

    /**
     * Notifica il cambiamento del testo nel campo nome.
     */
    data class OnNameChanged(val value: String) : RegisterEvent

    /**
     * Notifica il cambiamento del testo nel campo cognome.
     */
    data class OnSurnameChanged(val value: String) : RegisterEvent

    /**
     * Notifica il cambiamento del testo nel campo username.
     */
    data class OnUsernameChanged(val value: String) : RegisterEvent

    /**
     * Notifica il cambiamento del testo nel campo email.
     */
    data class OnEmailChanged(val value: String) : RegisterEvent

    /**
     * Notifica il cambiamento del testo nel campo password.
     */
    data class OnPasswordChanged(val value: String) : RegisterEvent

    /**
     * Notifica il cambiamento del testo nel campo conferma password.
     */
    data class OnConfirmPasswordChanged(val confirmPasswordValue: String) : RegisterEvent

    /**
     * Scatena il tentativo di registrazione.
     */
    data object OnRegisterClicked : RegisterEvent
}