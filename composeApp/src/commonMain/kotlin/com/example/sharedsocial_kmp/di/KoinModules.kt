package com.example.sharedsocial_kmp.di

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.core.dispatchers.RealAppDispatchers
import com.example.sharedsocial_kmp.data.local.AuthPersistence
import com.example.sharedsocial_kmp.data.local.AuthPersistenceImpl
import com.example.sharedsocial_kmp.data.repository.KtorAuthRepository
import com.example.sharedsocial_kmp.domain.repository.AuthRepository
import com.example.sharedsocial_kmp.domain.usecase.IsUserAuthenticatedUseCaseImpl
import com.example.sharedsocial_kmp.domain.usecase.LoginUseCase
import com.example.sharedsocial_kmp.domain.usecase.LoginUseCaseImpl
import com.example.sharedsocial_kmp.domain.usecase.IsUserAuthenticatedUseCase
import com.example.sharedsocial_kmp.navigation.AppNavigator
import com.example.sharedsocial_kmp.navigation.AppNavigatorImpl
import com.example.sharedsocial_kmp.ui.features.home.HomeViewModel
import com.example.sharedsocial_kmp.ui.features.login.LoginViewModel
import com.example.sharedsocial_kmp.ui.features.root.RootViewModel
import org.koin.dsl.module

/**
 * Modulo Koin per la definizione delle dipendenze condivise (Common).
 * Configura i singleton per i servizi core e le factory per UseCase e ViewModel.
 */
val commonModule = module {

    // Core & Navigation
    single<AppNavigator> { AppNavigatorImpl() }
    single<AppDispatchers> { RealAppDispatchers() }

    // Data & Persistence
    single<AuthPersistence> { AuthPersistenceImpl(get(), get()) }
    single<AuthRepository> { KtorAuthRepository(get(), get(), get()) }

    // UseCases
    factory<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
    factory<IsUserAuthenticatedUseCase> { IsUserAuthenticatedUseCaseImpl(get()) }


    // ViewModels
    factory { RootViewModel(get(), get()) }
    factory { LoginViewModel(get(), get(), get()) }
    factory { HomeViewModel(get()) }
}