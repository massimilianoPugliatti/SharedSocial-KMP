package com.example.sharedsocial_kmp.features.register.domain.usecase

/**
 * Rappresenta la logica di business per l'operazione di registrazione.
 * Astrae la chiamata al repository fornendo un punto d'accesso unico per la UI.
 */
interface RegisterUseCase {
    /**
     * Esegue il processo di registrazione.
     * Restituisce un successo o un fallimento in caso di dati non validi o problemi di rete.
     */
    suspend operator fun invoke(
        name: String,
        surname: String,
        username: String,
        email: String,
        pass: String
    ): Result<String>
}