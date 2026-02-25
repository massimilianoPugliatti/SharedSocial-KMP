package com.example.sharedsocial_kmp.ui.features.login

/**
 * Rappresenta lo stato immutabile della schermata di login.
 * Contiene i dati inseriti dall'utente, gli stati di caricamento e i messaggi di errore
 * necessari per il rendering della UI.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val isSuccess: Boolean = false
) {
    /**
     * Determina se l'azione di login può essere eseguita.
     * Basato sulla validità sintattica dei campi e sullo stato attuale del processo.
     */
    val canLogin: Boolean get() = email.isNotBlank() &&
            password.length >= 6 &&
            !isLoading
}