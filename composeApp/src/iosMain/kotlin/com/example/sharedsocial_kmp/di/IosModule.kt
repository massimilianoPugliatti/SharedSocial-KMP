package com.example.sharedsocial_kmp.di

import com.example.sharedsocial_kmp.data.local.IosSecureStorage
import com.example.sharedsocial_kmp.data.local.SecureStorage
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Funzione di utilità per inizializzare Koin dal lato iOS (Swift).
 * Registra i moduli comuni e quelli specifici per la piattaforma Apple.
 */
fun initKoin() {
    startKoin {
        modules(commonModule + iosModule + networkModule)
    }
}

/**
 * Modulo specifico per iOS.
 * Qui vengono registrate le implementazioni native come IosSecureStorage.
 */
val iosModule = module {
    single<SecureStorage> { IosSecureStorage(get()) }
}