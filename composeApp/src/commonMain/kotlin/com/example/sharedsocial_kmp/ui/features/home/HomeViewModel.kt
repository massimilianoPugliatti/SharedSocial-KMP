package com.example.sharedsocial_kmp.ui.features.home

import cafe.adriel.voyager.core.model.ScreenModel
import com.example.sharedsocial_kmp.navigation.AppNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel(
    private val navigator: AppNavigator // La tua interfaccia di navigazione
) : ScreenModel {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

}