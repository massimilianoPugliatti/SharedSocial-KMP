package com.example.sharedsocial_kmp.features.camera.presentation

import com.example.sharedsocial_kmp.features.camera.domain.model.CameraMode
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

data class CameraState(
    val isLoading: Boolean = false,
    val isRecording: Boolean = false,
    val selectedMode: CameraMode = CameraMode.PHOTO,
    val elapsedRecordingSeconds: Long = 0L,
    val capturedMedia: MediaAsset? = null,
    val uiMessage: CameraUiMessage? = null,
    val requestPermissions: Boolean = false,
)