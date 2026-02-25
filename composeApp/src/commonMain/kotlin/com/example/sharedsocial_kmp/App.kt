package com.example.sharedsocial_kmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.example.sharedsocial_kmp.navigation.AppNavigator
import com.example.sharedsocial_kmp.navigation.NavigationAction
import com.example.sharedsocial_kmp.ui.features.root.RootScreen
import org.koin.compose.koinInject

/**
 * Punto di ingresso principale dell'applicazione Compose Multiplatform.
 * Configura il tema e inizializza il sistema di navigazione Voyager,
 * mettendo in relazione gli eventi astratti di [AppNavigator] con il navigatore concreto.
 */
@Composable
fun App() {
    MaterialTheme {
        val appNavigator = koinInject<AppNavigator>()

        Navigator(RootScreen()) { voyagerNavigator ->
            /**
             * Osserva il flusso di eventi di navigazione provenienti dal modulo condiviso.
             * Traduce le [NavigationAction] in chiamate API specifiche di Voyager.
             */
            LaunchedEffect(voyagerNavigator) {
                appNavigator.navigationEvents.collect { action ->
                    when (action) {
                        is NavigationAction.ReplaceAll -> voyagerNavigator.replaceAll(action.screen)
                        is NavigationAction.Push -> voyagerNavigator.push(action.screen)
                        is NavigationAction.Pop -> voyagerNavigator.pop()
                    }
                }
            }

            CurrentScreen()
        }
    }
}