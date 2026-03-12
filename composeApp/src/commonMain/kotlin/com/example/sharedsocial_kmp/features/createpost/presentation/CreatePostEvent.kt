package com.example.sharedsocial_kmp.features.createpost.presentation

sealed interface CreatePostEvent {
    data class OnCaptionChanged(val value: String) : CreatePostEvent
    data object OnSubmitClick : CreatePostEvent
    data object OnBackClick : CreatePostEvent
    data object OnMessageConsumed : CreatePostEvent
}