package com.example.sharedsocial_kmp.features.auth.domain.usecase

import com.example.sharedsocial_kmp.features.auth.data.local.AuthPersistence

/**
 * Verifica se esiste una sessione utente valida e persistita.
 */
class IsUserAuthenticatedUseCaseImpl(
    private val authPersistence: AuthPersistence
): IsUserAuthenticatedUseCase {
    override suspend operator fun invoke(): Boolean = authPersistence.isAuthenticated()
}