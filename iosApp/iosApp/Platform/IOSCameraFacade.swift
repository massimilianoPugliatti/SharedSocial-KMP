//
//  IOSCameraFacade.swift
//  iosApp
//
//  Created by Massimiliano Pugliatti on 09/03/26.
//

import UIKit
import ComposeApp
import AVFoundation

final class IOSCameraFacade: NSObject, CameraService, CameraPermissionService {

    private let engine = IOSCameraEngine()

    override init() {
        super.init()
        try? engine.configureSessionIfNeeded()
    }

    func makePreviewController() -> UIViewController {
        return engine.makePreviewController()
    }

    func start() async throws -> any CameraResult {
        return CameraResultSuccess(value: KotlinUnit())
    }

    func stop() async throws -> any CameraResult {
        engine.stopSession()
        return CameraResultSuccess(value: KotlinUnit())
    }

    func switchCamera() async throws -> any CameraResult {
        do {
            try engine.switchCamera()
            return CameraResultSuccess(value: KotlinUnit())
        } catch {
            return CameraResultFailure(error: CameraErrorCameraUnavailable())
        }
    }

    func capturePhoto() async throws -> any CameraResult {
        return await withCheckedContinuation { continuation in
            engine.capturePhoto { path in
                guard let path else {
                    continuation.resume(
                        returning: CameraResultFailure(error: CameraErrorCaptureFailed())
                    )
                    return
                }

                let media = MediaAssetPhoto(
                    localPath: path,
                    mimeType: "image/jpeg"
                )

                continuation.resume(
                    returning: CameraResultSuccess(value: media)
                )
            }
        }
    }

    func startVideoRecording() async throws -> any CameraResult {
        engine.startRecording()
        return CameraResultSuccess(value: KotlinUnit())
    }

    func stopVideoRecording() async throws -> any CameraResult {
        return await withCheckedContinuation { continuation in
            engine.stopRecording { path in
                guard let path else {
                    continuation.resume(
                        returning: CameraResultFailure(error: CameraErrorCaptureFailed())
                    )
                    return
                }

                let media = MediaAssetVideo(
                    localPath: path,
                    mimeType: "video/mp4",
                    durationMillis: nil
                )

                continuation.resume(
                    returning: CameraResultSuccess(value: media)
                )
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
                        returning: CameraResultFailure(error: CameraErrorPermissionDenied())
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
                        returning: CameraResultFailure(error: CameraErrorMicrophonePermissionDenied())
                    )
                }
            }
        }
    }
}
