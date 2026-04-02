package com.example.sharedsocial_kmp.platform

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.sharedsocial_kmp.core.platform.MediaAssetPayload
import com.example.sharedsocial_kmp.core.platform.MediaAssetReader
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import java.io.File
import java.io.IOException
import java.util.UUID
import androidx.core.net.toUri

class AndroidMediaAssetReader(
    private val context: Context,
) : MediaAssetReader {

    override suspend fun read(media: MediaAsset): Result<MediaAssetPayload> = runCatching {
        val uri = resolveUri(media.localPath)
        val fileName = resolveFileName(uri, media)
        val bytes = readBytes(uri, media.localPath)

        MediaAssetPayload(
            fileName = fileName,
            bytes = bytes,
            mimeType = media.mimeType,
        )
    }

    private fun resolveUri(path: String): Uri? {
        return when {
            path.startsWith("content://") || path.startsWith("file://") -> path.toUri()
            else -> null
        }
    }

    private fun resolveFileName(uri: Uri?, media: MediaAsset): String {
        if (uri != null && uri.scheme == "content") {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0 && cursor.moveToFirst()) {
                        val displayName = cursor.getString(nameIndex)
                        if (!displayName.isNullOrBlank()) return displayName
                    }
                }
        }

        val fromUri = uri?.lastPathSegment?.substringAfterLast('/')
        if (!fromUri.isNullOrBlank()) return fromUri

        val fromPath = media.localPath.substringAfterLast('/').substringAfterLast('\\')
        if (fromPath.isNotBlank() && fromPath != media.localPath) return fromPath

        return "upload_${UUID.randomUUID()}${extensionFor(media.mimeType)}"
    }

    private fun readBytes(uri: Uri?, path: String): ByteArray {
        if (uri != null) {
            context.contentResolver.openInputStream(uri)?.use { input ->
                return input.readBytes()
            }
        }

        val file = File(path)
        if (!file.exists()) {
            throw IOException("File non trovato: $path")
        }
        return file.readBytes()
    }

    private fun extensionFor(mimeType: String): String = when {
        mimeType.contains("png") -> ".png"
        mimeType.contains("webp") -> ".webp"
        mimeType.contains("mp4") -> ".mp4"
        mimeType.contains("quicktime") -> ".mov"
        mimeType.contains("webm") -> ".webm"
        else -> ".jpg"
    }
}
