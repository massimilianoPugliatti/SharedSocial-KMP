package com.example.sharedsocial_kmp.features.camera.presentation

import com.example.sharedsocial_kmp.features.camera.domain.model.CameraMode

sealed interface CameraEvent {
    data object OnStart : CameraEvent
    data object OnStop : CameraEvent
    data object OnBackClick : CameraEvent
    data object OnTakePhotoClick : CameraEvent
    data object OnStartRecordingClick : CameraEvent
    data object OnStopRecordingClick : CameraEvent
    data object OnSwitchCameraClick : CameraEvent
    data object OnPickMediaClick : CameraEvent
    data class OnModeChanged(val mode: CameraMode) : CameraEvent
    data object OnMessageConsumed : CameraEvent
    data object OnCapturedMediaConsumed : CameraEvent
}