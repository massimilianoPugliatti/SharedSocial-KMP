package com.example.sharedsocial_kmp.features.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

/**
 * Rappresenta la schermata di Login all'interno del sistema di navigazione Voyager.
 * Si occupa di inizializzare il [LoginViewModel] tramite Koin e di osservarne lo stato
 * per passarlo al componente visuale [LoginContent].
 */
class LoginScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<LoginViewModel>()
        val state by viewModel.state.collectAsState()

        LoginContent(
            state = state,
            onEvent = { event -> viewModel.onEvent(event) }
        )
    }
}