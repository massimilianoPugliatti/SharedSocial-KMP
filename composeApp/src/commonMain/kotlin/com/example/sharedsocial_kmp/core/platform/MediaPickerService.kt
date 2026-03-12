package com.example.sharedsocial_kmp.core.platform

import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

interface MediaPickerService {
    suspend fun pickImageOrVideo(): CameraResult<MediaAsset>
}