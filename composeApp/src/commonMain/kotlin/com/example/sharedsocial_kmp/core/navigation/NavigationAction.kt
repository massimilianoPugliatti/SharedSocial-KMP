package com.example.sharedsocial_kmp.core.navigation

import cafe.adriel.voyager.core.screen.Screen

/**
 * Definisce le azioni di navigazione possibili all'interno dell'applicazione.
 * Viene utilizzata per trasmettere intenzioni di navigazione dai ViewModel alla UI.
 */
sealed class NavigationAction {
    /**
     * Sostituisce l'intero stack di navigazione con una nuova schermata.
     */
    data class ReplaceAll(val screen: Screen) : NavigationAction()

    /**
     * Aggiunge una nuova schermata sopra quella attuale nello stack.
     */
    data class Push(val screen: Screen) : NavigationAction()

    /**
     * Rimuove la schermata corrente per tornare a quella precedente.
     */
    data object Pop : NavigationAction()
}