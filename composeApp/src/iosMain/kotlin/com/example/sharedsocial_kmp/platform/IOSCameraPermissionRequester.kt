package com.example.sharedsocial_kmp.platform

import com.example.sharedsocial_kmp.core.platform.CameraPermissionRequester
import com.example.sharedsocial_kmp.core.platform.CameraPermissionRequestResult
import com.example.sharedsocial_kmp.core.platform.CameraPermissionService
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult

class IOSCameraPermissionRequester(
    private val permissionService: CameraPermissionService,
) : CameraPermissionRequester {

    override suspend fun requestPermissions(
        needsMicrophone: Boolean,
    ): CameraPermissionRequestResult {

        val cameraGranted =
            permissionService.ensureCameraPermission() is CameraResult.Success

        val microphoneGranted =
            if (needsMicrophone) {
                permissionService.ensureMicrophonePermission() is CameraResult.Success
            } else {
                true
            }

        return CameraPermissionRequestResult(
            cameraGranted = cameraGranted,
            microphoneGranted = microphoneGranted
        )
    }
}