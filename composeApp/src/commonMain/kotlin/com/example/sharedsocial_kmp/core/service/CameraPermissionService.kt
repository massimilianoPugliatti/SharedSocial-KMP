package com.example.sharedsocial_kmp.core.service

import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult

interface CameraPermissionService {
    suspend fun ensureCameraPermission(): CameraResult<Unit>
    suspend fun ensureMicrophonePermission(): CameraResult<Unit>
}