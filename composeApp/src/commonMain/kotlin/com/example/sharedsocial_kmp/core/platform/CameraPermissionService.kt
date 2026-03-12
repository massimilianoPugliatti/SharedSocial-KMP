package com.example.sharedsocial_kmp.core.platform

import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult

interface CameraPermissionService {
    suspend fun ensureCameraPermission(): CameraResult<Unit>
    suspend fun ensureMicrophonePermission(): CameraResult<Unit>
}