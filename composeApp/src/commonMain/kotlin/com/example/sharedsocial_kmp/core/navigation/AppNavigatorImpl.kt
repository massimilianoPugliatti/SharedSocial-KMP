package com.example.sharedsocial_kmp.core.navigation

import com.example.sharedsocial_kmp.features.auth.presentation.LoginScreen
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import com.example.sharedsocial_kmp.features.createpost.presentation.CreatePostScreen
import com.example.sharedsocial_kmp.features.home.presentation.HomePagerScreen
import com.example.sharedsocial_kmp.features.register.presentation.RegisterScreen
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
        _events.trySend(NavigationAction.ReplaceAll(HomePagerScreen()))
    }

    override fun navigateToLogin() {
        _events.trySend(NavigationAction.ReplaceAll(LoginScreen()))
    }

    override fun navigateToRegister() {
        _events.trySend(NavigationAction.Push(RegisterScreen()))
    }

    override fun goBack() {
        _events.trySend(NavigationAction.Pop)
    }

    override fun navigateToProfile(userId: Long) {
        // Implementazione futura della navigazione al profilo
    }

    override fun navigateToComments(postId: Long) {
        // Implementazione futura della navigazione ai commenti

    }

    override fun navigateToCreatePost(value: MediaAsset) {
      _events.trySend(NavigationAction.Push(CreatePostScreen(value)))
    }
}