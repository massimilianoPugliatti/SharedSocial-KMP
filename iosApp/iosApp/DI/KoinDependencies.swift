//
//  KoinDependencies.swift
//  iosApp
//
//  Created by Massimiliano Pugliatti on 01/03/26.
//


import Foundation
import ComposeApp

enum KoinDependencies {

    static func start() {
        let swiftStorage = IosSecureStorage()
        let analyticsService = IosAnalyticsService(isDebug: true)

        let cameraFacade = IOSCameraFacade()
        let previewRenderer = IOSCameraPreviewRenderer(
            controllerProvider: {
                cameraFacade.makePreviewController()
            }
        )
        let mediaPickerService = IOSMediaPickerService()

        IosModuleKt.doInitKoin(
            secureStorage: swiftStorage,
            analyticsService: analyticsService,
            cameraService: cameraFacade,
            cameraPreviewRenderer: previewRenderer,
            cameraPermissionService: cameraFacade,
            mediaPickerService: mediaPickerService
        )
    }
}
