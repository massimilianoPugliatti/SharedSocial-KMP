package com.example.sharedsocial_kmp.features.register.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sharedsocial_kmp.core.ui.AppTextField
import org.jetbrains.compose.resources.painterResource
import sharedsocialkmp.composeapp.generated.resources.Res
import sharedsocialkmp.composeapp.generated.resources.icona

/**
 * Componente visuale principale per la schermata di registrazione.
 * Organizza il layout in sezioni logiche e delega la gestione degli input
 * e delle azioni attraverso il pattern Unidirectional Data Flow (UDF).
 */
@Composable
fun RegisterContent(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit
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
            RegisterHeader()

            Spacer(Modifier.height(32.dp))

            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp).testTag("global_error")
                )
            }

            RegisterFields(
                state = state,
                onEvent = onEvent,
                isEnabled = !state.isLoading
            )

            Spacer(Modifier.height(24.dp))

            RegisterActions(
                canRegister = state.canRegister,
                isLoading = state.isLoading,
                onEvent = onEvent
            )
        }
    }
}


/**
 * Visualizza l'identità visiva della schermata, inclusi logo e messaggio di benvenuto.
 */
@Composable
private fun RegisterHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(Res.drawable.icona),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Unspecified
        )
        Text("Benvenuto!", style = MaterialTheme.typography.headlineMedium)
    }
}

/**
 * Contiene i campi di input per i dati di registrazione.
 */
@Composable
private fun RegisterFields(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit,
    isEnabled: Boolean
) {

    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.semantics() {
        // Fondamentale: aiuta il sistema a identificare il form
        isTraversalGroup = true
    },) {
        AppTextField(
            value = state.name,
            onValueChange = { onEvent(RegisterEvent.OnNameChanged(it)) },
            label = "Nome",
            tag = "name_field",
            enabled = isEnabled,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
           contentType = ContentType.PersonFirstName,
        )
        AppTextField(
            value = state.surname,
            onValueChange = { onEvent(RegisterEvent.OnSurnameChanged(it)) },
            label = "Cognome",
            tag = "surname_field",
            enabled = isEnabled,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            contentType = ContentType.PersonLastName
        )
        AppTextField(
            value = state.username,
            onValueChange = { onEvent(RegisterEvent.OnUsernameChanged(it)) },
            label = "Username",
            tag = "username_field",
            enabled = isEnabled,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            contentType = ContentType.NewUsername
        )
        AppTextField(
            value = state.email,
            onValueChange = { onEvent(RegisterEvent.OnEmailChanged(it)) },
            label = "Email",
            tag = "email_field",
            enabled = isEnabled,
            error = state.emailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            contentType = ContentType.EmailAddress
        )
        AppTextField(
            value = state.password,
            onValueChange = { onEvent(RegisterEvent.OnPasswordChanged(it)) },
            label = "Password",
            isPassword = true,
            tag = "password_field",
            enabled = isEnabled,
            error = state.passwordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            contentType = ContentType.NewPassword
        )
        AppTextField(
            value = state.confirmPassword,
            onValueChange = { onEvent(RegisterEvent.OnConfirmPasswordChanged(it)) },
            label = "Conferma Password",
            isPassword = true,
            tag = "confirm_password_field",
            enabled = isEnabled,
            error = state.confirmPasswordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            contentType = ContentType.NewPassword
        )
    }
}

/**
 * Gestisce i pulsanti di azione e lo stato di caricamento durante la registrazione.
 */
@Composable
private fun RegisterActions(
    canRegister: Boolean,
    isLoading: Boolean,
    onEvent: (RegisterEvent) -> Unit
) {
    Button(
        onClick = { onEvent(RegisterEvent.OnRegisterClicked) },
        modifier = Modifier.fillMaxWidth().testTag("register_button"),
        enabled = canRegister
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp).testTag("register_loader"),
                color = Color.White
            )
        } else {
            Text("Registrati")
        }
    }
}