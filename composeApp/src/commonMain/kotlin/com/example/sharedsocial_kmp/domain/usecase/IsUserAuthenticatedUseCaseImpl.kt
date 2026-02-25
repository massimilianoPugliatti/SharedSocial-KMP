package com.example.sharedsocial_kmp.domain.usecase

import com.example.sharedsocial_kmp.data.local.AuthPersistence
import com.example.sharedsocial_kmp.domain.usecase.IsUserAuthenticatedUseCase

/**
 * Verifica se esiste una sessione utente valida e persistita.
 */
class IsUserAuthenticatedUseCaseImpl(
    private val authPersistence: AuthPersistence
): IsUserAuthenticatedUseCase {
    override suspend operator fun invoke(): Boolean = authPersistence.isAuthenticated()
}