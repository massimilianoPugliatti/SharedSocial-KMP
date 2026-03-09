package com.example.sharedsocial_kmp.di

import com.example.sharedsocial_kmp.core.di.commonModule
import com.example.sharedsocial_kmp.core.network.networkModule
import com.example.sharedsocial_kmp.features.auth.data.local.SecureStorage
import com.example.sharedsocial_kmp.core.service.AnalyticsService
import com.example.sharedsocial_kmp.core.service.CameraPermissionService
import com.example.sharedsocial_kmp.core.service.CameraPreviewRenderer
import com.example.sharedsocial_kmp.core.service.CameraService
import com.example.sharedsocial_kmp.core.service.MediaPickerService
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Funzione di utilità per inizializzare Koin dal lato iOS (Swift).
 * Registra i moduli comuni e quelli specifici per la piattaforma Apple.
 */
fun initKoin(
    secureStorage: SecureStorage, analyticsService: AnalyticsService, cameraService: CameraService,
    cameraPreviewRenderer: CameraPreviewRenderer,
    cameraPermissionService: CameraPermissionService,
    mediaPickerService: MediaPickerService,
) {
    startKoin {
        modules(
            commonModule + networkModule + module {
                single<SecureStorage> { secureStorage }
                single<AnalyticsService> { analyticsService }
            } + cameraIosModule(
                cameraService = cameraService,
                cameraPreviewRenderer = cameraPreviewRenderer,
                permissionService = cameraPermissionService,
                mediaPickerService = mediaPickerService
            )
        )
    }
}