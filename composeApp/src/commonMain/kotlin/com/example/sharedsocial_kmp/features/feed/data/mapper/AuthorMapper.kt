package com.example.sharedsocial_kmp.features.feed.data.mapper

import com.example.sharedsocial_kmp.features.feed.data.remote.dto.UserDto
import com.example.sharedsocial_kmp.features.feed.domain.model.Author

/**
 * Mappa l'oggetto [UserDto] nel modello di dominio [Author].
 */
fun UserDto.toDomain(): Author {
    return Author(
        id = this.id,
        username = this.username
    )
}