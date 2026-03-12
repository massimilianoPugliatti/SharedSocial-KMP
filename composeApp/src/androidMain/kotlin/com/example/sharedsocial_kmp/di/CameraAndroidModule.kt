package com.example.sharedsocial_kmp.di

import com.example.sharedsocial_kmp.core.platform.CameraPermissionService
import com.example.sharedsocial_kmp.core.platform.CameraPreviewRenderer
import com.example.sharedsocial_kmp.core.platform.CameraService
import com.example.sharedsocial_kmp.platform.AndroidCameraFacade
import com.example.sharedsocial_kmp.platform.AndroidMediaPickerService
import com.example.sharedsocial_kmp.features.camera.domain.usecase.CapturePhotoUseCase
import com.example.sharedsocial_kmp.features.camera.domain.usecase.CapturePhotoUseCaseImpl
import com.example.sharedsocial_kmp.features.camera.domain.usecase.PickMediaUseCase
import com.example.sharedsocial_kmp.features.camera.domain.usecase.PickMediaUseCaseImpl
import com.example.sharedsocial_kmp.features.camera.domain.usecase.StartVideoRecordingUseCase
import com.example.sharedsocial_kmp.features.camera.domain.usecase.StartVideoRecordingUseCaseImpl
import com.example.sharedsocial_kmp.features.camera.domain.usecase.StopVideoRecordingUseCase
import com.example.sharedsocial_kmp.features.camera.domain.usecase.StopVideoRecordingUseCaseImpl
import com.example.sharedsocial_kmp.features.camera.domain.usecase.SwitchCameraUseCase
import com.example.sharedsocial_kmp.features.camera.domain.usecase.SwitchCameraUseCaseImpl
import com.example.sharedsocial_kmp.features.camera.presentation.CameraViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

val cameraAndroidModule = module {
    single {
        AndroidCameraFacade(context = get())
    } binds arrayOf(
        CameraService::class,
        CameraPreviewRenderer::class,
        CameraPermissionService::class
    )

    single { AndroidMediaPickerService(get()) }

    factoryOf(::CapturePhotoUseCaseImpl) bind CapturePhotoUseCase::class
    factoryOf(::StartVideoRecordingUseCaseImpl) bind StartVideoRecordingUseCase::class
    factoryOf(::StopVideoRecordingUseCaseImpl) bind StopVideoRecordingUseCase::class
    factoryOf(::SwitchCameraUseCaseImpl) bind SwitchCameraUseCase::class
    factoryOf(::PickMediaUseCaseImpl) bind PickMediaUseCase::class

    factory { CameraViewModel(get(), get(), get(), get(), get(), get(), get(), get(),get(),get()) }
}