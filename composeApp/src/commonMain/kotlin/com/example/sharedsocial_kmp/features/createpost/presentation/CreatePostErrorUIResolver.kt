package com.example.sharedsocial_kmp.features.createpost.presentation

import com.example.sharedsocial_kmp.features.createpost.domain.model.CreatePostError

object CreatePostErrorUIResolver {
    fun mapToMessage(error: Throwable): String = when (error) {
        is CreatePostError.NetworkError -> "Problema di connessione"
        is CreatePostError.UnsupportedMedia -> "Formato media non supportato"
        is CreatePostError.MediaReadFailed -> "Impossibile leggere il file selezionato"
        is CreatePostError.Unauthorized -> "Sessione scaduta"
        is CreatePostError.Forbidden -> "Operazione non consentita"
        is CreatePostError.ServerError -> "Il server non risponde"
        is CreatePostError.BadRequest -> "Impossibile pubblicare il post"
        else -> "Impossibile pubblicare il post"
    }
}
