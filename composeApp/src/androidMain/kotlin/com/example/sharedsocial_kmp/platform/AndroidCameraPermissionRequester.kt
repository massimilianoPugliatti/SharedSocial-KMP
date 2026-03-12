package com.example.sharedsocial_kmp.platform

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import com.example.sharedsocial_kmp.core.platform.CameraPermissionRequestResult
import com.example.sharedsocial_kmp.core.platform.CameraPermissionRequester
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidCameraPermissionRequester : CameraPermissionRequester {

    private var launcher: ActivityResultLauncher<Array<String>>? = null
    private var pendingContinuation:
        ((CameraPermissionRequestResult) -> Unit)? = null

    fun attachLauncher(launcher: ActivityResultLauncher<Array<String>>) {
        this.launcher = launcher
    }

    fun onPermissionsResult(result: Map<String, Boolean>) {
        val continuation = pendingContinuation ?: return
        pendingContinuation = null

        continuation(
            CameraPermissionRequestResult(
                cameraGranted = result[Manifest.permission.CAMERA] == true,
                microphoneGranted = result[Manifest.permission.RECORD_AUDIO] == true
            )
        )
    }

    override suspend fun requestPermissions(
        needsMicrophone: Boolean
    ): CameraPermissionRequestResult {
        val launcher = launcher ?: return CameraPermissionRequestResult(
            cameraGranted = false,
            microphoneGranted = !needsMicrophone
        )

        val permissions = buildList {
            add(Manifest.permission.CAMERA)
            if (needsMicrophone) {
                add(Manifest.permission.RECORD_AUDIO)
            }
        }.toTypedArray()

        return suspendCancellableCoroutine { cont ->
            pendingContinuation = { result ->
                cont.resume(result)
            }
            launcher.launch(permissions)
        }
    }
}