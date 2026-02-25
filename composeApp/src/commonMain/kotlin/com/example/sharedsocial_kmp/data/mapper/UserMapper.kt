package com.example.sharedsocial_kmp.data.mapper

import com.example.sharedsocial_kmp.data.remote.dto.UserDto
import com.example.sharedsocial_kmp.domain.model.User

/**
 * Mappa l'oggetto [UserDto] nel modello di dominio [User].
 * Gestisce la concatenazione del nome e la conversione dell'identificativo.
 */
fun UserDto.toDomain(): User {
    return User(
        id = this.id.toString(),
        fullName = "${this.nome} ${this.cognome}",
        email = this.email
    )
}