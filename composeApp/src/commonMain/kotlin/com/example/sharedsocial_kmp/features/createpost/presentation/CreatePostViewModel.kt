package com.example.sharedsocial_kmp.features.createpost.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import com.example.sharedsocial_kmp.core.navigation.AppNavigator
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import com.example.sharedsocial_kmp.features.createpost.domain.model.CreatePostDraft
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreatePostViewModel(
    private val navigator: AppNavigator,
    initialMedia: MediaAsset,
) : ScreenModel {

    private val _state = MutableStateFlow(
        CreatePostState(
            media = initialMedia
        )
    )
    val state = _state.asStateFlow()

    fun onEvent(event: CreatePostEvent) {
        when (event) {
            is CreatePostEvent.OnCaptionChanged -> {
                _state.update { it.copy(caption = event.value) }
            }

            CreatePostEvent.OnSubmitClick -> {
                submitDraft()
            }

            CreatePostEvent.OnBackClick -> {
                navigator.goBack()
            }

            CreatePostEvent.OnMessageConsumed -> {
                _state.update { it.copy(message = null) }
            }
        }
    }

    private fun submitDraft() {
        val current = _state.value

        val draft = CreatePostDraft(
            caption = current.caption.trim(),
            media = current.media
        )

        println("CreatePostDraft pronto: $draft")

        _state.update {
            it.copy(
                message = "Bozza pronta. Il submit al repository verrà aggiunto nel prossimo step."
            )
        }
    }
}