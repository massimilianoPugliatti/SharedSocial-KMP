package com.example.sharedsocial_kmp.navigation

import com.example.sharedsocial_kmp.ui.features.home.HomeScreen
import com.example.sharedsocial_kmp.ui.features.login.LoginScreen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Implementazione concreta del navigatore basata su eventi.
 * Invia segnali di navigazione attraverso un [Channel] che verranno interpretati
 * dal sistema di navigazione della UI (es. Voyager).
 */
class AppNavigatorImpl : AppNavigator {

    private val _events = Channel<NavigationAction>(Channel.BUFFERED)
    override val navigationEvents = _events.receiveAsFlow()

    override fun navigateToHome() {
        _events.trySend(NavigationAction.ReplaceAll(HomeScreen()))
    }

    override fun navigateToLogin() {
        _events.trySend(NavigationAction.ReplaceAll(LoginScreen()))
    }

    override fun goBack() {
        _events.trySend(NavigationAction.Pop)
    }

    override fun navigateToProfile(userId: String) {
        // Implementazione futura della navigazione al profilo
    }
}