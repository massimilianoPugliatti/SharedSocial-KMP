package com.example.sharedsocial_kmp.di

import com.example.sharedsocial_kmp.core.di.commonModule
import com.example.sharedsocial_kmp.core.network.networkModule
import com.example.sharedsocial_kmp.features.auth.data.local.SecureStorage
import com.example.sharedsocial_kmp.core.platform.AnalyticsService
import com.example.sharedsocial_kmp.core.platform.CameraPermissionRequester
import com.example.sharedsocial_kmp.core.platform.CameraPermissionService
import com.example.sharedsocial_kmp.core.platform.CameraPreviewRenderer
import com.example.sharedsocial_kmp.core.platform.CameraService
import com.example.sharedsocial_kmp.core.platform.MediaPickerService
import com.example.sharedsocial_kmp.core.platform.MediaPreviewRenderer
import com.example.sharedsocial_kmp.platform.IOSCameraPermissionRequester
import com.example.sharedsocial_kmp.platform.IOSMediaPreviewRenderer
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
                single<MediaPreviewRenderer> { IOSMediaPreviewRenderer() }
                single<CameraPermissionRequester>{
                    IOSCameraPermissionRequester(
                        cameraPermissionService
                    )
                }
            } + cameraIosModule(
                cameraService = cameraService,
                cameraPreviewRenderer = cameraPreviewRenderer,
                permissionService = cameraPermissionService,
                mediaPickerService = mediaPickerService
            )
        )
    }
}