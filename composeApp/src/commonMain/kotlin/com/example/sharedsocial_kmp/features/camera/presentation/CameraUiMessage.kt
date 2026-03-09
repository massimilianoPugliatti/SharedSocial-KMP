package com.example.sharedsocial_kmp.features.camera.presentation

sealed interface CameraUiMessage {
    data object PermissionDenied : CameraUiMessage
    data object CaptureFailed : CameraUiMessage
    data object CameraUnavailable : CameraUiMessage
    data object RecordingAlreadyStarted : CameraUiMessage
    data object RecordingNotStarted : CameraUiMessage
    data class Generic(val message: String) : CameraUiMessage
}