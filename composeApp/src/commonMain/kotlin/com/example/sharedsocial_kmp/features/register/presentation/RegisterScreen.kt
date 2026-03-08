package com.example.sharedsocial_kmp.features.register.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

/**
 * Rappresenta la schermata di registrazione all'interno del sistema di navigazione Voyager.
 * Si occupa di inizializzare il [RegisterViewModel] tramite Koin e di osservarne lo stato
 * per passarlo al componente visuale [RegisterContent].
 */
class RegisterScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<RegisterViewModel>()
        val state by viewModel.state.collectAsState()

        RegisterContent(
            state = state,
            onEvent = { event -> viewModel.onEvent(event) }
        )
    }
}