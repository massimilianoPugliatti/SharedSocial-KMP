package com.example.sharedsocial_kmp.data.service

import android.app.Activity
import android.content.Context
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
    private var context: Context
) : MediaPickerService {

    private var launcher: ActivityResultLauncher<Intent>? = null
    private var pendingContinuation: ((CameraResult<MediaAsset>) -> Unit)? = null

    fun updateContext(newContext: Context) {
        this.context = newContext
    }

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

        val mime = context.contentResolver.getType(uri).orEmpty()
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
        val currentLauncher = launcher ?: return CameraResult.Failure(
            CameraError.Unknown("Picker launcher non inizializzato")
        )

        return suspendCancellableCoroutine { cont ->
            pendingContinuation = { result -> cont.resume(result) }

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            currentLauncher.launch(intent)
        }
    }
}