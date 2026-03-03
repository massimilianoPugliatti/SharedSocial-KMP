package com.example.sharedsocial_kmp.features.feed.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.core.navigation.AppNavigator
import com.example.sharedsocial_kmp.features.feed.domain.model.FeedContext
import com.example.sharedsocial_kmp.features.feed.domain.usecase.GetPostsUseCase
import com.example.sharedsocial_kmp.features.feed.domain.usecase.NewPostUseCase
import com.example.sharedsocial_kmp.features.feed.domain.usecase.ToggleLikeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val navigator: AppNavigator,
    private val getPostsUseCase: GetPostsUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val newPostUseCase: NewPostUseCase,
    private val dispatchers: AppDispatchers,
) : ScreenModel {

    private val _state = MutableStateFlow(FeedState())
    val state = _state.asStateFlow()

    init {
        loadPosts()
    }

    fun onEvent(event: FeedEvent) {
        when (event) {
            is FeedEvent.OnNewPostContentChanged -> updateNewPostContent(event.value)
            is FeedEvent.OnPostCommentCliked -> goToComments(event.id)
            is FeedEvent.OnPostLikeCliked -> toggleLike(event.id)
            FeedEvent.OnRefreshTriggered -> loadPosts()
            FeedEvent.OnNewPostButtonClicked -> performNewPost()
            FeedEvent.OnErrorConsumed -> _state.update { it.copy(error = null) }
        }
    }

    private fun loadPosts() {
        screenModelScope.launch(dispatchers.main) {
            _state.update { it.copy(isLoading = true) }
            getPostsUseCase()
                .onSuccess { fetchedPosts ->
                    _state.update {
                        it.copy(
                            posts = fetchedPosts,
                            isLoading = false
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            error = FeedErrorUIResolver.mapToMessage(error, FeedContext.LOADING_FEED),
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun toggleLike(idPost: Long) {
        screenModelScope.launch(dispatchers.main) {
            _state.update {
                it.copy(posts = it.posts.map { post ->
                    if (post.id == idPost) {
                        val newLikedStatus = !post.liked
                        post.copy(
                            liked = newLikedStatus,
                            likesCount = if (newLikedStatus) post.likesCount + 1 else post.likesCount - 1
                        )
                    } else post
                })
            }
            toggleLikeUseCase(idPost)
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            posts = it.posts.map { post ->
                                if (post.id == idPost) {
                                    val newLikedStatus = !post.liked
                                    post.copy(
                                        liked = newLikedStatus,
                                        likesCount = if (newLikedStatus) post.likesCount + 1 else post.likesCount - 1
                                    )
                                } else post
                            },
                            error = FeedErrorUIResolver.mapToMessage(error, FeedContext.TOGGLING_LIKE)
                        )
                    }
                }

        }
    }

    private fun goToComments(id: Long) {
        navigator.navigateToComments(id)
    }

    private fun performNewPost() {
        val currentText = state.value.newPostText
        if (currentText.isBlank() || state.value.isLoading) return

        screenModelScope.launch(dispatchers.main) {
            _state.update { it.copy(isLoading = true) }
            newPostUseCase(currentText).onSuccess {
                _state.update { it.copy(newPostText = "", isLoading = false) }
                loadPosts()
            }.onFailure { e ->
                _state.update { it.copy(
                    isLoading = false,
                    error = FeedErrorUIResolver.mapToMessage(e, FeedContext.CREATING_POST)
                )}
            }
        }
    }

    private fun updateNewPostContent(value: String) {
        screenModelScope.launch(dispatchers.main) {
            _state.update {
                it.copy(newPostText = value)
            }
        }

    }

}