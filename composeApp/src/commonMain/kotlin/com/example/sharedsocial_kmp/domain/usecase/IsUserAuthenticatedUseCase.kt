package com.example.sharedsocial_kmp.domain.usecase

interface IsUserAuthenticatedUseCase {

    suspend operator fun invoke(): Boolean
}