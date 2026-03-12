package com.example.sharedsocial_kmp.core.platform

interface CameraPermissionRequester {
    suspend fun requestPermissions(
        needsMicrophone: Boolean,
    ): CameraPermissionRequestResult
}

data class CameraPermissionRequestResult(
    val cameraGranted: Boolean,
    val microphoneGranted: Boolean,
)

