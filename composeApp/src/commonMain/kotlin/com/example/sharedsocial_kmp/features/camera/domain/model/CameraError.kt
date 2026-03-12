package com.example.sharedsocial_kmp.features.camera.domain.model

sealed interface CameraError {
    data object PermissionDenied : CameraError
    data object CameraUnavailable : CameraError
    data object MicrophonePermissionDenied : CameraError
    data object RecordingAlreadyStarted : CameraError
    data object RecordingNotStarted : CameraError
    data object CaptureFailed : CameraError
    data object PickerCancelled : CameraError
    data class Unknown(val message: String? = null) : CameraError
}