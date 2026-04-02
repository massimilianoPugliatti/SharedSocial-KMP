package com.example.sharedsocial_kmp.features.createpost.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.core.navigation.AppNavigator
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import com.example.sharedsocial_kmp.features.createpost.domain.model.CreatePostDraft
import com.example.sharedsocial_kmp.features.createpost.domain.usecase.CreatePostUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatePostViewModel(
    private val navigator: AppNavigator,
    private val createPostUseCase: CreatePostUseCase,
    private val dispatchers: AppDispatchers,
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
                if (!_state.value.isSubmitting) {
                    navigator.goBack()
                }
            }

            CreatePostEvent.OnMessageConsumed -> {
                _state.update { it.copy(message = null) }
            }
        }
    }

    private fun submitDraft() {
        val current = _state.value
        if (current.isSubmitting) return

        val draft = CreatePostDraft(
            caption = current.caption.trim(),
            media = current.media
        )

        screenModelScope.launch(dispatchers.main) {
            _state.update { it.copy(isSubmitting = true, message = null) }

            createPostUseCase(draft)
                .onSuccess {
                    _state.update { it.copy(isSubmitting = false) }
                    navigator.navigateToHome()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            message = CreatePostErrorUIResolver.mapToMessage(error)
                        )
                    }
                }
        }
    }
}
