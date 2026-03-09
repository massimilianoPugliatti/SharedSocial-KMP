package com.example.sharedsocial_kmp.features.camera.domain.usecase

import com.example.sharedsocial_kmp.core.service.CameraService
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult

interface SwitchCameraUseCase {
    suspend operator fun invoke(): CameraResult<Unit>
}

class SwitchCameraUseCaseImpl(
    private val cameraService: CameraService
) : SwitchCameraUseCase {
    override suspend fun invoke(): CameraResult<Unit> = cameraService.switchCamera()
}