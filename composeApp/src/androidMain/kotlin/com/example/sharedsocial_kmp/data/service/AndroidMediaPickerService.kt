package com.example.sharedsocial_kmp.data.service

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.example.sharedsocial_kmp.core.service.MediaPickerService
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraError
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidMediaPickerService(
    private val activity: Activity
) : MediaPickerService {

    private var launcher: ActivityResultLauncher<Intent>? = null
    private var pendingContinuation: ((CameraResult<MediaAsset>) -> Unit)? = null

    fun attachLauncher(launcher: ActivityResultLauncher<Intent>) {
        this.launcher = launcher
    }

    fun onActivityResult(result: ActivityResult) {
        val callback = pendingContinuation ?: return
        pendingContinuation = null

        val uri: Uri? = result.data?.data

        if (result.resultCode != Activity.RESULT_OK || uri == null) {
            callback(CameraResult.Failure(CameraError.PickerCancelled))
            return
        }

        val mime = activity.contentResolver.getType(uri).orEmpty()
        val value = uri.toString()

        val media = if (mime.startsWith("video")) {
            MediaAsset.Video(localPath = value, mimeType = mime)
        } else {
            MediaAsset.Photo(
                localPath = value,
                mimeType = mime.ifBlank { "image/*" }
            )
        }

        callback(CameraResult.Success(media))
    }

    override suspend fun pickImageOrVideo(): CameraResult<MediaAsset> {
        val launcher = launcher
            ?: return CameraResult.Failure(
                CameraError.Unknown("Picker launcher non inizializzato")
            )

        return suspendCancellableCoroutine { cont ->
            pendingContinuation = { result -> cont.resume(result) }

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
            }

            launcher.launch(intent)
        }
    }
}