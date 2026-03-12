package com.example.sharedsocial_kmp.features.createpost.presentation

import com.example.sharedsocial_kmp.features.createpost.domain.model.CreatePostDraft

sealed interface CreatePostAction {
    data class Submit(val draft: CreatePostDraft) : CreatePostAction
    data object NavigateBack : CreatePostAction
}