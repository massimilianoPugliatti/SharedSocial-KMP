//
//  IOSCameraEngineError.swift
//  iosApp
//
//  Created by Massimiliano Pugliatti on 10/03/26.
//

import Foundation

enum IOSCameraEngineError: Error {
    case notConfigured
    case cameraUnavailable
    case microphoneUnavailable
    case captureFailed
    case recordingAlreadyStarted
    case recordingNotStarted
    case permissionDenied
    case unknown(String?)
}
