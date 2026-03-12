import ComposeApp
//
//  IOSMediaPickerService.swift
//  iosApp
//
//  Created by Massimiliano Pugliatti on 09/03/26.
//
import Foundation
import PhotosUI
import UIKit
import UniformTypeIdentifiers

final class IOSMediaPickerService: NSObject, MediaPickerService, PHPickerViewControllerDelegate {

    private var continuation: CheckedContinuation<any CameraResult, Never>?

    func pickImageOrVideo() async throws -> any CameraResult {
        await withCheckedContinuation { continuation in
            self.continuation = continuation

            var configuration = PHPickerConfiguration(photoLibrary: .shared())
            configuration.selectionLimit = 1
            configuration.filter = .any(of: [.images, .videos])

            let picker = PHPickerViewController(configuration: configuration)
            picker.delegate = self

            guard
                let root = UIApplication.shared.connectedScenes
                    .compactMap({ $0 as? UIWindowScene })
                    .flatMap({ $0.windows })
                    .first(where: \.isKeyWindow)?
                    .rootViewController
            else {
                continuation.resume(
                    returning: CameraResultFailure(
                        error: CameraErrorUnknown(message: "RootViewController non disponibile")
                    )
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

        if item.itemProvider.hasItemConformingToTypeIdentifier(UTType.image.identifier) {
            handleImage(item: item, continuation: continuation)
            return
        }

        if item.itemProvider.hasItemConformingToTypeIdentifier(UTType.movie.identifier) {
            handleVideo(item: item, continuation: continuation)
            return
        }

        continuation.resume(
            returning: CameraResultFailure(error: CameraErrorCaptureFailed())
        )
    }

    private func handleImage(
        item: PHPickerResult,
        continuation: CheckedContinuation<any CameraResult, Never>
    ) {
        item.itemProvider.loadFileRepresentation(forTypeIdentifier: UTType.image.identifier) { url, error in
            guard let url else {
                continuation.resume(
                    returning: CameraResultFailure(error: CameraErrorCaptureFailed())
                )
                return
            }

            guard
                let copiedUrl = self.copyPickedFileToTemp(
                    sourceUrl: url,
                    preferredExtension: url.pathExtension.isEmpty ? "jpg" : url.pathExtension
                )
            else {
                continuation.resume(
                    returning: CameraResultFailure(error: CameraErrorCaptureFailed())
                )
                return
            }

            let mimeType = self.mimeTypeForImageExtension(copiedUrl.pathExtension)

            let media = MediaAssetPhoto(
                localPath: copiedUrl.absoluteString,
                mimeType: mimeType
            )

            continuation.resume(
                returning: CameraResultSuccess(value: media)
            )
        }
    }

    private func handleVideo(
        item: PHPickerResult,
        continuation: CheckedContinuation<any CameraResult, Never>
    ) {
        item.itemProvider.loadFileRepresentation(forTypeIdentifier: UTType.movie.identifier) { url, error in
            guard let url else {
                continuation.resume(
                    returning: CameraResultFailure(error: CameraErrorCaptureFailed())
                )
                return
            }

            guard
                let copiedUrl = self.copyPickedFileToTemp(
                    sourceUrl: url,
                    preferredExtension: url.pathExtension.isEmpty ? "mp4" : url.pathExtension
                )
            else {
                continuation.resume(
                    returning: CameraResultFailure(error: CameraErrorCaptureFailed())
                )
                return
            }

            let media = MediaAssetVideo(
                localPath: copiedUrl.absoluteString,
                mimeType: "video/mp4",
                durationMillis: nil
            )

            continuation.resume(
                returning: CameraResultSuccess(value: media)
            )
        }
    }

    private func copyPickedFileToTemp(
        sourceUrl: URL,
        preferredExtension: String
    ) -> URL? {
        let fileManager = FileManager.default

        let destinationUrl = URL(fileURLWithPath: NSTemporaryDirectory())
            .appendingPathComponent("picked_\(UUID().uuidString).\(preferredExtension)")

        do {
            if fileManager.fileExists(atPath: destinationUrl.path) {
                try fileManager.removeItem(at: destinationUrl)
            }

            try fileManager.copyItem(at: sourceUrl, to: destinationUrl)
            print("Picker file copied to: \(destinationUrl.absoluteString)")
            return destinationUrl
        } catch {
            print("Errore copia file picker: \(error)")
            return nil
        }
    }

    private func mimeTypeForImageExtension(_ ext: String) -> String {
        switch ext.lowercased() {
        case "png":
            return "image/png"
        case "heic":
            return "image/heic"
        case "jpeg", "jpg":
            return "image/jpeg"
        default:
            return "image/jpeg"
        }
    }
}
