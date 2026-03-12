package com.example.sharedsocial_kmp.core.platform

import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset

interface CameraService {
    suspend fun start(): CameraResult<Unit>
    suspend fun stop(): CameraResult<Unit>
    suspend fun capturePhoto(): CameraResult<MediaAsset.Photo>
    suspend fun startVideoRecording(): CameraResult<Unit>
    suspend fun stopVideoRecording(): CameraResult<MediaAsset.Video>
    suspend fun switchCamera(): CameraResult<Unit>
}