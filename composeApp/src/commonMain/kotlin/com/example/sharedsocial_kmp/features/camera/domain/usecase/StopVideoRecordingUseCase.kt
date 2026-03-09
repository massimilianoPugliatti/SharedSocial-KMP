package com.example.sharedsocial_kmp.features.camera.domain.usecase

import com.example.sharedsocial_kmp.core.service.CameraService
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

interface StopVideoRecordingUseCase {
    suspend operator fun invoke(): CameraResult<MediaAsset.Video>
}

class StopVideoRecordingUseCaseImpl(
    private val cameraService: CameraService
) : StopVideoRecordingUseCase {
    override suspend fun invoke(): CameraResult<MediaAsset.Video> = cameraService.stopVideoRecording()
}