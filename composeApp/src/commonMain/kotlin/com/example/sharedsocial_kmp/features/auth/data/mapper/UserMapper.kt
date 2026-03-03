package com.example.sharedsocial_kmp.features.auth.data.mapper

import com.example.sharedsocial_kmp.features.auth.data.remote.dto.UserInfoDto
import com.example.sharedsocial_kmp.features.auth.domain.model.User

/**
 * Mappa l'oggetto [UserInfoDto] nel modello di dominio [User].
 */
fun UserInfoDto.toDomain(): User {
    return User(
        id = this.id,
        fullName = "${this.nome} ${this.cognome}",
        email = this.email
    )
}