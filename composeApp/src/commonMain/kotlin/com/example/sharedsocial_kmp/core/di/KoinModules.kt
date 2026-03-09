package com.example.sharedsocial_kmp.core.di


import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.core.dispatchers.RealAppDispatchers
import com.example.sharedsocial_kmp.features.auth.data.local.AuthPersistence
import com.example.sharedsocial_kmp.features.auth.data.local.AuthPersistenceImpl
import com.example.sharedsocial_kmp.features.auth.data.repository.AuthRepositoryDecorator
import com.example.sharedsocial_kmp.features.auth.data.repository.KtorAuthRepository
import com.example.sharedsocial_kmp.features.auth.domain.repository.AuthRepository
import com.example.sharedsocial_kmp.core.service.PermissionService
import com.example.sharedsocial_kmp.core.service.PermissionServiceImpl
import com.example.sharedsocial_kmp.features.auth.domain.usecase.IsUserAuthenticatedUseCaseImpl
import com.example.sharedsocial_kmp.features.feed.domain.usecase.ToggleLikeUseCase
import com.example.sharedsocial_kmp.features.feed.domain.usecase.ToggleLikeUseCaseImpl
import com.example.sharedsocial_kmp.features.auth.domain.usecase.IsUserAuthenticatedUseCase
import com.example.sharedsocial_kmp.core.navigation.AppNavigator
import com.example.sharedsocial_kmp.core.navigation.AppNavigatorImpl
import com.example.sharedsocial_kmp.features.auth.domain.usecase.LoginUseCase
import com.example.sharedsocial_kmp.features.auth.domain.usecase.LoginUseCaseImpl
import com.example.sharedsocial_kmp.features.feed.presentation.FeedViewModel
import com.example.sharedsocial_kmp.features.auth.presentation.LoginViewModel
import com.example.sharedsocial_kmp.features.camera.presentation.CameraViewModel
import com.example.sharedsocial_kmp.features.feed.data.repository.FeedRepositoryDecorator
import com.example.sharedsocial_kmp.features.feed.data.repository.KtorFeedRepository
import com.example.sharedsocial_kmp.features.feed.domain.repository.FeedRepository
import com.example.sharedsocial_kmp.features.feed.domain.usecase.GetPostsUseCase
import com.example.sharedsocial_kmp.features.feed.domain.usecase.GetPostsUseCaseImpl
import com.example.sharedsocial_kmp.features.feed.domain.usecase.NewPostUseCase
import com.example.sharedsocial_kmp.features.feed.domain.usecase.NewPostuseCaseImpl
import com.example.sharedsocial_kmp.features.register.data.repository.KtorRegisterRepository
import com.example.sharedsocial_kmp.features.register.data.repository.RegisterRepositoryDecorator
import com.example.sharedsocial_kmp.features.register.domain.repository.RegisterRepository
import com.example.sharedsocial_kmp.features.register.domain.usecase.RegisterUseCase
import com.example.sharedsocial_kmp.features.register.domain.usecase.RegisterUseCaseImpl
import com.example.sharedsocial_kmp.features.register.presentation.RegisterViewModel
import com.example.sharedsocial_kmp.root.RootViewModel
import com.mmk.kmpnotifier.notification.NotifierManager
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Modulo Koin per la definizione delle dipendenze condivise (Common).
 * Configura i singleton per i servizi core e le factory per UseCase e ViewModel.
 */
val commonModule = module {

    // Core & Navigation
    single<AppNavigator> { AppNavigatorImpl() }
    single<AppDispatchers> { RealAppDispatchers() }
    single<PermissionService> {
        PermissionServiceImpl()
    }

    // Data & Persistence
    single<AuthPersistence> { AuthPersistenceImpl(get(), get()) }
    single<AuthRepository>(named("base_auth_repo")) {
        KtorAuthRepository(
            httpClient = get(),
            authPersistence = get(),
            dispatchers = get()
        )
    }
    single<AuthRepository> {
        AuthRepositoryDecorator(
            delegate = get(named("base_auth_repo")),
            analytics = get(),
            pushNotifier = NotifierManager.getPushNotifier(),
            permissionService = get()
        )
    }
    single<RegisterRepository>(named("base_register_repo")) {
        KtorRegisterRepository(
            httpClient = get(),
            dispatchers = get()
        )
    }
    single<RegisterRepository> {
        RegisterRepositoryDecorator(
            delegate = get(named("base_register_repo")),
            analytics = get(),
        )
    }
    single<FeedRepository> ( named("base_feed_repo")) {
        KtorFeedRepository(
            httpClient = get(),
            dispatchers = get()
        )
    }
    single<FeedRepository> {
        FeedRepositoryDecorator(
            delegate = get(named("base_feed_repo")),
            analytics = get()
        )
    }

    // UseCases
    factory<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
    factory<IsUserAuthenticatedUseCase> { IsUserAuthenticatedUseCaseImpl(get()) }
    factory<RegisterUseCase> { RegisterUseCaseImpl(get(), get()) }
    factory<GetPostsUseCase> { GetPostsUseCaseImpl(get(),get()) }
    factory<NewPostUseCase>{ NewPostuseCaseImpl(get(),get()) }
    factory<ToggleLikeUseCase>{ ToggleLikeUseCaseImpl(get(),get()) }



    // ViewModels
    factory { RootViewModel(get(), get()) }
    factory { LoginViewModel(get(), get(), get()) }
    factory { RegisterViewModel(get(), get(), get()) }
    factory { FeedViewModel(get(), get(), get(), get(), get()) }
    factory { CameraViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}