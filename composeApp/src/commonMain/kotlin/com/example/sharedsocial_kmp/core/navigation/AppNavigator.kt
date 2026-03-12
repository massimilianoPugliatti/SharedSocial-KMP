package com.example.sharedsocial_kmp.core.navigation

import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import kotlinx.coroutines.flow.Flow

/**
 * Gestore della navigazione astratto per il modulo condiviso.
 * Permette ai ViewModel di innescare transizioni tra schermi tramite un flusso di eventi,
 * mantenendo la logica di navigazione disaccoppiata dall'implementazione della UI.
 */
interface AppNavigator {
    /**
     * Flusso di azioni di navigazione osservato dai componenti della UI.
     */
    val navigationEvents: Flow<NavigationAction>

    /**
     * Reindirizza l'utente alla schermata principale.
     */
    fun navigateToHome()

    /**
     * Reindirizza l'utente alla schermata di login, tipicamente dopo un logout.
     */
    fun navigateToLogin()

    /**
     * Reindirizza l'utente alla schermata di registrazione.
     */
    fun navigateToRegister()

    /**
     * Torna alla schermata precedente nello stack di navigazione.
     */
    fun goBack()

    /**
     * Naviga verso il profilo di un utente specifico.
     */
    fun navigateToProfile(userId: Long)

    fun navigateToComments(postId: Long)
    fun navigateToCreatePost(value: MediaAsset)
}