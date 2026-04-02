package com.example.sharedsocial_kmp.features.feed.data.mapper

import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import com.example.sharedsocial_kmp.features.feed.data.remote.dto.PostDto
import com.example.sharedsocial_kmp.features.feed.domain.model.Post

/**
 * Mappa l'oggetto [PostDto] nel modello di dominio [Post].
 */
fun PostDto.toDomain(): Post {
    return Post(
        id = this.id,
        content = this.testo?.takeIf { it.isNotBlank() },
        media = this.toMediaAssetOrNull(),
        date = this.dataPubblicazione,
        author = this.autore.toDomain(),
        commentsCount = this.numeroCommenti,
        likesCount = this.numeroLikes,
        liked = this.liked,
        isMine = this.mine
    )
}

private fun PostDto.toMediaAssetOrNull(): MediaAsset? {
    val url = mediaUrl?.takeIf { it.isNotBlank() } ?: return null
    val contentType = mediaContentType?.takeIf { it.isNotBlank() }
    val normalizedType = mediaType?.uppercase()

    return when {
        normalizedType == "VIDEO" || contentType?.startsWith("video/") == true -> {
            MediaAsset.Video(
                localPath = url,
                mimeType = contentType ?: "video/mp4",
                durationMillis = null
            )
        }

        normalizedType == "IMAGE" || contentType?.startsWith("image/") == true -> {
            MediaAsset.Photo(
                localPath = url,
                mimeType = contentType ?: "image/jpeg"
            )
        }

        else -> null
    }
}