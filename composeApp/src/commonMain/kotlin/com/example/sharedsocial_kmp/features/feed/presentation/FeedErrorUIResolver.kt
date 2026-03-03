package com.example.sharedsocial_kmp.features.feed.presentation

import com.example.sharedsocial_kmp.features.feed.domain.model.FeedContext
import com.example.sharedsocial_kmp.features.feed.domain.model.FeedError


/**
 * Resover dedicato alla trasformazione degli errori di dominio in
 * messaggi leggibili dall'utente (Presentation Logic).
 */
object FeedErrorUIResolver {
    fun mapToMessage(error: Throwable, context: FeedContext): String = when (error) {
        is FeedError.NetworkError -> "Problema di connessione"
        is FeedError.Unauthorized -> "Sessione scaduta"
        is FeedError.ServerError -> "Il server non risponde"

        else -> when (context) {
            FeedContext.LOADING_FEED -> "Impossibile caricare i post. Trascina per aggiornare."
            FeedContext.CREATING_POST -> "Impossibile pubblicare il post."
            FeedContext.TOGGLING_LIKE -> "Impossibile aggiornare il like."
        }
    }
}