package com.example.sharedsocial_kmp.platform

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import com.example.sharedsocial_kmp.features.camera.data.CameraErrorMapper
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraError

object AndroidCameraErrorMapper {
    fun mapThrowable(error: Throwable): CameraError {
        return when (error) {
            is SecurityException -> CameraError.PermissionDenied
            is ImageCaptureException -> when (error.imageCaptureError) {
                ImageCapture.ERROR_CAMERA_CLOSED,
                ImageCapture.ERROR_INVALID_CAMERA -> CameraError.CameraUnavailable

                ImageCapture.ERROR_CAPTURE_FAILED,
                ImageCapture.ERROR_FILE_IO,
                ImageCapture.ERROR_UNKNOWN -> CameraError.CaptureFailed

                else -> CameraErrorMapper.mapThrowable(error)
            }

            else -> CameraErrorMapper.mapThrowable(error)
        }
    }
}