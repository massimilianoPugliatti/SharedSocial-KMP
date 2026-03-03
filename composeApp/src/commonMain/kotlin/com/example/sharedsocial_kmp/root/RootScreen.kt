package com.example.sharedsocial_kmp.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

/**
 * Schermata di ingresso (entry point) che gestisce lo smistamento iniziale dell'utente.
 * Visualizza un indicatore di caricamento mentre il [RootViewModel] verifica
 * lo stato della sessione per determinare la destinazione corretta.
 */
class RootScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<RootViewModel>()

        LaunchedEffect(Unit) {
            viewModel.checkAuth()
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}