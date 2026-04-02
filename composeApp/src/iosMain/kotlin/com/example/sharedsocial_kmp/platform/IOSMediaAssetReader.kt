package com.example.sharedsocial_kmp.platform

import com.example.sharedsocial_kmp.core.platform.MediaAssetPayload
import com.example.sharedsocial_kmp.core.platform.MediaAssetReader
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
class IOSMediaAssetReader : MediaAssetReader {

    override suspend fun read(media: MediaAsset): Result<MediaAssetPayload> = runCatching {
        val url = resolveUrl(media.localPath)
            ?: error("Percorso media non valido")

        val data = NSData.dataWithContentsOfURL(url)
            ?: error("Impossibile leggere il file selezionato")

        val fileName = url.lastPathComponent ?: "upload${extensionFor(media.mimeType)}"

        MediaAssetPayload(
            fileName = fileName,
            bytes = data.toByteArray(),
            mimeType = media.mimeType,
        )
    }

    private fun resolveUrl(path: String): NSURL? {
        return when {
            path.startsWith("file://") -> NSURL.URLWithString(path)
            path.startsWith("/") -> NSURL.fileURLWithPath(path)
            else -> NSURL.URLWithString(path)
        }
    }

    private fun NSData.toByteArray(): ByteArray {
        val size = length.toInt()
        if (size == 0) return ByteArray(0)

        val result = ByteArray(size)
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes, length)
        }
        return result
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
