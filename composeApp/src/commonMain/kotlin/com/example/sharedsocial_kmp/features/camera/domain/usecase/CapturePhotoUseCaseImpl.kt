package com.example.sharedsocial_kmp.features.camera.domain.usecase

import com.example.sharedsocial_kmp.core.platform.CameraService
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

class CapturePhotoUseCaseImpl(
    private val cameraService: CameraService,
) : CapturePhotoUseCase {
    override suspend fun invoke(): CameraResult<MediaAsset.Photo> = cameraService.capturePhoto()
}