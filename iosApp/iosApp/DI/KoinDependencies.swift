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
        IosModuleKt.doInitKoin(secureStorage: swiftStorage,analyticsService: IosAnalyticsService(isDebug: true))
    }
}
