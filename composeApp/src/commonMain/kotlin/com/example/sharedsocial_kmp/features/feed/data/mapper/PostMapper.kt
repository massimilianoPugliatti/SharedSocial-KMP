package com.example.sharedsocial_kmp.features.feed.data.mapper

import com.example.sharedsocial_kmp.features.feed.data.remote.dto.PostDto
import com.example.sharedsocial_kmp.features.feed.domain.model.Post

/**
 * Mappa l'oggetto [PostDto] nel modello di dominio [Post].
 */
fun PostDto.toDomain(): Post {
    return Post(
        id = this.id,
        content = this.testo,
        date = this.dataPubblicazione,
        author = this.autore.toDomain(),
        commentsCount = this.numeroCommenti,
        likesCount = this.numeroLikes,
        liked = this.liked,
        isMine = this.mine
    )
}