package com.example.sharedsocial_kmp.features.feed.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val username: String,
)
