package com.example.sharedsocial_kmp.features.camera.domain.usecase

import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult

interface StartVideoRecordingUseCase {
    suspend operator fun invoke(): CameraResult<Unit>
}