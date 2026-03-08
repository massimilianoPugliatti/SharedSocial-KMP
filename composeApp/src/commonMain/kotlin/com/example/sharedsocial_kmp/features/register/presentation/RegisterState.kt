package com.example.sharedsocial_kmp.features.register.presentation

/**
 * Rappresenta lo stato immutabile della schermata di registrazione.
 * Contiene i dati inseriti dall'utente, gli stati di caricamento e i messaggi di errore
 * necessari per il rendering della UI.
 */
data class RegisterState(
    val name: String = "",
    val surname: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isSuccess: Boolean = false
) {
    /**
     * Determina se l'azione di registrazione può essere eseguita.
     * Basato sulla validità sintattica dei campi e sullo stato attuale del processo.
     */
    val canRegister: Boolean
        get() = name.isNotBlank() &&
                surname.isNotBlank() &&
                username.isNotBlank() &&
                email.isNotBlank() &&
                emailError == null &&
                password.isNotBlank() &&
                passwordError == null &&
                confirmPassword.isNotBlank() &&
                confirmPasswordError == null &&
                password == confirmPassword &&
                !isLoading
}