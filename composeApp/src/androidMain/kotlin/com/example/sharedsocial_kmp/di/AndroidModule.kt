package com.example.sharedsocial_kmp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.sharedsocial_kmp.data.local.AndroidSecureStorage
import com.example.sharedsocial_kmp.features.auth.data.local.SecureStorage
import com.example.sharedsocial_kmp.data.service.AndroidAnalyticsService
import com.example.sharedsocial_kmp.core.service.AnalyticsService
import org.koin.dsl.module

/**
 * Definizione del modulo Koin per la piattaforma Android.
 * * Fornisce le implementazioni concrete delle dipendenze che richiedono l'accesso
 * alle API di sistema e al [Context].
 */
val androidModule = module {

    /**
     * Fornisce un'istanza singleton di [DataStore] configurata per Android.
     */
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = { get<Context>().preferencesDataStoreFile("auth_prefs") }
        )
    }

    /**
     * Fornisce l'implementazione [AndroidSecureStorage] per la gestione cifrata dei dati.
     * * @see SecureStorage
     */
    single<SecureStorage> {
        AndroidSecureStorage(
            context = get(),
            dataStore = get(),
            dispatchers = get()
        )
    }

    /**
     * Forniscono i servizi Firebase nativi per Android.
     */
    single<AnalyticsService> {
        AndroidAnalyticsService(
            context = get(),
            isDebug = true
        )
    }
}