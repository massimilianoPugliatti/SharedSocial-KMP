package com.example.sharedsocial_kmp.features.feed.domain.usecase

/**
 * Rappresenta la logica di business per l'operazione toggle like.
 * Astrae la chiamata al repository fornendo un punto d'accesso unico per la UI.
 */
interface ToggleLikeUseCase {
    /**
     * Esegue il processo di toggle like.
     * Restituisce un successo o un fallimento.
     */
    suspend operator fun invoke(idPost:Long): Result<Unit>
}