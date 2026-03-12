package com.example.sharedsocial_kmp.features.camera.presentation

import com.example.sharedsocial_kmp.features.camera.domain.model.CameraError

/**
 * Resolver dedicato alla traduzione degli errori di dominio della camera
 * in messaggi/feedback UI.
 */
object CameraErrorUiResolver {

    fun resolve(error: CameraError): CameraUiMessage {
        return when (error) {
            CameraError.PermissionDenied -> CameraUiMessage.PermissionDenied
            CameraError.MicrophonePermissionDenied -> CameraUiMessage.MicrophonePermissionDenied
            CameraError.CameraUnavailable -> CameraUiMessage.CameraUnavailable
            CameraError.CaptureFailed -> CameraUiMessage.CaptureFailed
            CameraError.RecordingAlreadyStarted -> CameraUiMessage.RecordingAlreadyStarted
            CameraError.RecordingNotStarted -> CameraUiMessage.RecordingNotStarted
            CameraError.PickerCancelled -> CameraUiMessage.PickerCancelled
            is CameraError.Unknown -> CameraUiMessage.Generic(
                error.message ?: "Errore sconosciuto"
            )
        }
    }
}