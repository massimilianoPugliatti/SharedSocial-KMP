package com.example.sharedsocial_kmp.ui.features.home

data class HomeState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val isSuccess: Boolean = false)