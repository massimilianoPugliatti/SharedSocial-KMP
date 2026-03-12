package com.example.sharedsocial_kmp.features.camera.domain.usecase

import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

interface CapturePhotoUseCase {
    suspend operator fun invoke(): CameraResult<MediaAsset.Photo>
}
