package com.example.sharedsocial_kmp.di

import com.example.sharedsocial_kmp.core.di.commonModule
import com.example.sharedsocial_kmp.core.network.networkModule
import com.example.sharedsocial_kmp.features.auth.data.local.SecureStorage
import com.example.sharedsocial_kmp.core.service.AnalyticsService
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