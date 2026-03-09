package com.example.sharedsocial_kmp.features.camera.domain.usecase

import com.example.sharedsocial_kmp.core.service.CameraService
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult

interface StartVideoRecordingUseCase {
    suspend operator fun invoke(): CameraResult<Unit>
}

class StartVideoRecordingUseCaseImpl(
    private val cameraService: CameraService
) : StartVideoRecordingUseCase {
    override suspend fun invoke(): CameraResult<Unit> = cameraService.startVideoRecording()
}