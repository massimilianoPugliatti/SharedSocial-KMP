package com.example.sharedsocial_kmp.features.auth.domain.usecase

interface IsUserAuthenticatedUseCase {

    suspend operator fun invoke(): Boolean
}