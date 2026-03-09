package com.example.sharedsocial_kmp.features.camera.domain.usecase

import com.example.sharedsocial_kmp.core.service.MediaPickerService
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

interface PickMediaUseCase {
    suspend operator fun invoke(): CameraResult<MediaAsset>
}

class PickMediaUseCaseImpl(
    private val mediaPickerService: MediaPickerService
) : PickMediaUseCase {
    override suspend fun invoke(): CameraResult<MediaAsset> = mediaPickerService.pickImageOrVideo()
}