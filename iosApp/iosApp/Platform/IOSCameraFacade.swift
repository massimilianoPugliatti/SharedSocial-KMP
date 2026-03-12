//
//  IOSCameraFacade.swift
//  iosApp
//
//  Created by Massimiliano Pugliatti on 09/03/26.
//

import AVFoundation
import ComposeApp
import UIKit

final class IOSCameraFacade: NSObject, CameraService, CameraPermissionService {

    private let engine = IOSCameraEngine()
    private var isConfigured = false

    override init() {
        super.init()
    }

    func makePreviewController() -> UIViewController {
        return engine.makePreviewController()
    }

    func start() async throws -> any CameraResult {
        configureIfNeeded()
        engine.startSessionIfNeeded()
        return CameraResultSuccess(value: KotlinUnit())
    }

    func stop() async throws -> any CameraResult {
        engine.stopSession()
        return CameraResultSuccess(value: KotlinUnit())
    }

    func switchCamera() async throws -> any CameraResult {
        engine.switchCamera()
        return CameraResultSuccess(value: KotlinUnit())
    }

    func capturePhoto() async throws -> any CameraResult {
        return await withCheckedContinuation { continuation in
            engine.capturePhoto { result in
                switch result {
                case .success(let path):
                    let media = MediaAssetPhoto(
                        localPath: path,
                        mimeType: "image/jpeg"
                    )

                    continuation.resume(
                        returning: CameraResultSuccess(value: media)
                    )

                case .failure(let error):
                    continuation.resume(
                        returning: CameraResultFailure(
                            error: self.mapPlatformError(error)
                        )
                    )
                }
            }
        }
    }

    func startVideoRecording() async throws -> any CameraResult {
        if let error = engine.startRecording() {
            return CameraResultFailure(error: mapPlatformError(error))
        }

        return CameraResultSuccess(value: KotlinUnit())
    }

    func stopVideoRecording() async throws -> any CameraResult {
        return await withCheckedContinuation { continuation in
            engine.stopRecording { result in
                switch result {
                case .success(let path):
                    let media = MediaAssetVideo(
                        localPath: path,
                        mimeType: "video/mp4",
                        durationMillis: nil
                    )

                    continuation.resume(
                        returning: CameraResultSuccess(value: media)
                    )

                case .failure(let error):
                    continuation.resume(
                        returning: CameraResultFailure(
                            error: self.mapPlatformError(error)
                        )
                    )
                }
            }
        }
    }

    func ensureCameraPermission() async throws -> any CameraResult {
        return await withCheckedContinuation { continuation in
            AVCaptureDevice.requestAccess(for: .video) { granted in
                if granted {
                    continuation.resume(
                        returning: CameraResultSuccess(value: KotlinUnit())
                    )
                } else {
                    continuation.resume(
                        returning: CameraResultFailure(
                            error: CameraErrorPermissionDenied()
                        )
                    )
                }
            }
        }
    }

    func ensureMicrophonePermission() async throws -> any CameraResult {
        return await withCheckedContinuation { continuation in
            AVCaptureDevice.requestAccess(for: .audio) { granted in
                if granted {
                    continuation.resume(
                        returning: CameraResultSuccess(value: KotlinUnit())
                    )
                } else {
                    continuation.resume(
                        returning: CameraResultFailure(
                            error: CameraErrorMicrophonePermissionDenied()
                        )
                    )
                }
            }
        }
    }

    private func configureIfNeeded() {
        guard !isConfigured else { return }
        engine.configureSessionIfNeeded()
        isConfigured = true
    }

    private func mapPlatformError(_ error: IOSCameraEngineError) -> any CameraError {
        switch error {
        case .notConfigured:
            return CameraErrorCameraUnavailable()

        case .cameraUnavailable:
            return CameraErrorCameraUnavailable()

        case .microphoneUnavailable:
            return CameraErrorMicrophonePermissionDenied()

        case .captureFailed:
            return CameraErrorCaptureFailed()

        case .recordingAlreadyStarted:
            return CameraErrorRecordingAlreadyStarted()

        case .recordingNotStarted:
            return CameraErrorRecordingNotStarted()

        case .permissionDenied:
            return CameraErrorPermissionDenied()

        case .unknown(let message):
            return CameraErrorUnknown(message: message)
        }
    }
}
