package com.example.sharedsocial_kmp.features.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sharedsocial_kmp.core.ui.AppTextField
import org.jetbrains.compose.resources.painterResource
import sharedsocialkmp.composeapp.generated.resources.Res
import sharedsocialkmp.composeapp.generated.resources.icona

/**
 * Componente visuale principale per la schermata di login.
 * Organizza il layout in sezioni logiche e delega la gestione degli input
 * e delle azioni attraverso il pattern Unidirectional Data Flow (UDF).
 */
@Composable
fun LoginContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginHeader()

            Spacer(Modifier.height(32.dp))

            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp).testTag("global_error")
                )
            }

            LoginFields(
                state = state,
                onEvent = onEvent,
                isEnabled = !state.isLoading
            )

            Spacer(Modifier.height(24.dp))

            LoginActions(
                canLogin = state.canLogin,
                isLoading = state.isLoading,
                onEvent = onEvent
            )
            Spacer(Modifier.height(16.dp))

            LoginFooter(
                isEnabled = !state.isLoading,
                onEvent = onEvent
            )

        }
    }
}


/**
 * Visualizza l'identità visiva della schermata, inclusi logo e messaggio di benvenuto.
 */
@Composable
private fun LoginHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(Res.drawable.icona),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Unspecified
        )
        Text("Bentornato!", style = MaterialTheme.typography.headlineMedium)
    }
}

/**
 * Contiene i campi di input per le credenziali dell'utente.
 */
@Composable
private fun LoginFields(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    isEnabled: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AppTextField(
            value = state.email,
            onValueChange = { onEvent(LoginEvent.OnEmailChanged(it)) },
            label = "Email",
            tag = "email_field",
            enabled = isEnabled,
            error = state.emailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.semantics{
                contentType = ContentType.EmailAddress
            }

            ,
            //contentType = ContentType.EmailAddress
        )
        AppTextField(
            value = state.password,
            onValueChange = { onEvent(LoginEvent.OnPasswordChanged(it)) },
            label = "Password",
            isPassword = true,
            tag = "password_field",
            enabled = isEnabled,
            error = state.passwordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            contentType = ContentType.Password
        )
    }
}

/**
 * Gestisce i pulsanti di azione e lo stato di caricamento durante l'autenticazione.
 */
@Composable
private fun LoginActions(canLogin: Boolean, isLoading: Boolean, onEvent: (LoginEvent) -> Unit) {
    Button(
        onClick = { onEvent(LoginEvent.OnLoginClicked) },
        modifier = Modifier.fillMaxWidth().testTag("login_button"),
        enabled = canLogin
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp).testTag("login_loader"),
                color = Color.White
            )
        } else {
            Text("Accedi")
        }
    }
}

/**
 * Gestisce il link alla registrazione per i nuovi utenti.
 */
@Composable
private fun LoginFooter(isEnabled: Boolean, onEvent: (LoginEvent) -> Unit) {
    androidx.compose.material3.TextButton(
        onClick = { onEvent(LoginEvent.OnRegisterClick) },
        enabled = isEnabled,
        modifier = Modifier.testTag("go_to_register_button")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Non hai un account?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Registrati",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}