package com.example.sharedsocial_kmp.features.camera.domain.usecase

import com.example.sharedsocial_kmp.core.platform.MediaPickerService
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

class PickMediaUseCaseImpl(
    private val mediaPickerService: MediaPickerService,
) : PickMediaUseCase {
    override suspend fun invoke(): CameraResult<MediaAsset> = mediaPickerService.pickImageOrVideo()
}