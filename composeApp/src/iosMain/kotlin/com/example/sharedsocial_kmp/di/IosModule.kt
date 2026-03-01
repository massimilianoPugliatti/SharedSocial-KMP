package com.example.sharedsocial_kmp.di

import com.example.sharedsocial_kmp.data.local.SecureStorage
import com.example.sharedsocial_kmp.domain.service.AnalyticsService
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Funzione di utilità per inizializzare Koin dal lato iOS (Swift).
 * Registra i moduli comuni e quelli specifici per la piattaforma Apple.
 */
fun initKoin(secureStorage: SecureStorage, analyticsService: AnalyticsService) {
    startKoin {
        modules(commonModule + networkModule +  module {
            single<SecureStorage> { secureStorage }
            single<AnalyticsService> { analyticsService }
        })
    }
}