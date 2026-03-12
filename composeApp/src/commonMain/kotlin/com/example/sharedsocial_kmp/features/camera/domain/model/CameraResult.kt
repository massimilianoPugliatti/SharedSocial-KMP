package com.example.sharedsocial_kmp.features.camera.domain.model

sealed interface CameraResult<out T> {
    data class Success<T>(val value: T) : CameraResult<T>
    data class Failure(val error: CameraError) : CameraResult<Nothing>
}