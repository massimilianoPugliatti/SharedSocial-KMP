//
//  IOSMediaPickerService.swift
//  iosApp
//
//  Created by Massimiliano Pugliatti on 09/03/26.
//


import Foundation
import UIKit
import PhotosUI
import UniformTypeIdentifiers
import ComposeApp

final class IOSMediaPickerService: NSObject, MediaPickerService, PHPickerViewControllerDelegate {

    private var continuation: CheckedContinuation<any CameraResult, Never>?

    func pickImageOrVideo() async throws -> any CameraResult {
        return await withCheckedContinuation { continuation in
            self.continuation = continuation

            var configuration = PHPickerConfiguration(photoLibrary: .shared())
            configuration.selectionLimit = 1
            configuration.filter = .any(of: [.images, .videos])

            let picker = PHPickerViewController(configuration: configuration)
            picker.delegate = self

            guard let root = UIApplication.shared.connectedScenes
                .compactMap({ $0 as? UIWindowScene })
                .flatMap({ $0.windows })
                .first(where: \.isKeyWindow)?
                .rootViewController else {
                continuation.resume(
                    returning: CameraResultFailure(error: CameraErrorUnknown(message: "RootViewController non disponibile"))
                )
                return
            }

            root.present(picker, animated: true)
        }
    }

    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        picker.dismiss(animated: true)

        guard let continuation = continuation else { return }
        self.continuation = nil

        guard let item = results.first else {
            continuation.resume(
                returning: CameraResultFailure(error: CameraErrorPickerCancelled())
            )
            return
        }

        if item.itemProvider.hasItemConformingToTypeIdentifier(UTType.movie.identifier) {
            item.itemProvider.loadFileRepresentation(forTypeIdentifier: UTType.movie.identifier) { url, error in
                guard let url else {
                    continuation.resume(
                        returning: CameraResultFailure(error: CameraErrorCaptureFailed())
                    )
                    return
                }

                let media = MediaAssetVideo(
                    localPath: url.absoluteString,
                    mimeType: "video/mp4",
                    durationMillis: nil
                )

                continuation.resume(
                    returning: CameraResultSuccess(value: media)
                )
            }
        } else if item.itemProvider.hasItemConformingToTypeIdentifier(UTType.image.identifier) {
            item.itemProvider.loadFileRepresentation(forTypeIdentifier: UTType.image.identifier) { url, error in
                guard let url else {
                    continuation.resume(
                        returning: CameraResultFailure(error: CameraErrorCaptureFailed())
                    )
                    return
                }

                let media = MediaAssetPhoto(
                    localPath: url.absoluteString,
                    mimeType: "image/jpeg"
                )

                continuation.resume(
                    returning: CameraResultSuccess(value: media)
                )
            }
        } else {
            continuation.resume(
                returning: CameraResultFailure(error: CameraErrorCaptureFailed())
            )
        }
    }
}