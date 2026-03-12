package com.example.sharedsocial_kmp.features.camera.data

import com.example.sharedsocial_kmp.features.camera.domain.model.CameraError

/**
 * Mapper dedicato alla conversione delle eccezioni tecniche
 * in errori di dominio della feature camera.
 */
object CameraErrorMapper {
    fun mapThrowable(error: Throwable): CameraError {
        return when (error) {
            is IllegalStateException -> CameraError.CameraUnavailable
            else -> CameraError.Unknown(error.message)
        }
    }
}