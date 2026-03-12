package com.example.sharedsocial_kmp.features.camera.domain.usecase

import com.example.sharedsocial_kmp.core.platform.CameraService
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult

class SwitchCameraUseCaseImpl(
    private val cameraService: CameraService,
) : SwitchCameraUseCase {
    override suspend fun invoke(): CameraResult<Unit> = cameraService.switchCamera()
}