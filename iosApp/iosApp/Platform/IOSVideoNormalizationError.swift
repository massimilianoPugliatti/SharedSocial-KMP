//
//  IOSVideoNormalizationError.swift
//  iosApp
//
//  Created by Massimiliano Pugliatti on 02/04/26.
//


//
//  IOSVideoNormalizer.swift
//  iosApp
//
//  Created by OpenAI on 02/04/26.
//

import AVFoundation
import Foundation

enum IOSVideoNormalizationError: LocalizedError {
    case exportSessionUnavailable
    case mp4OutputNotSupported
    case exportFailed(String)
    case cancelled

    var errorDescription: String? {
        switch self {
        case .exportSessionUnavailable:
            return "Impossibile creare la sessione di export video"
        case .mp4OutputNotSupported:
            return "Il video non può essere esportato in formato MP4"
        case .exportFailed(let message):
            return "Esportazione video fallita: \(message)"
        case .cancelled:
            return "Esportazione video annullata"
        }
    }
}

final class IOSVideoNormalizer {

    static let shared = IOSVideoNormalizer()

    private init() {}

    func normalizeToMp4(sourceUrl: URL) async throws -> URL {
        let asset = AVURLAsset(url: sourceUrl)
        let preset = try choosePreset(for: asset)

        guard let exportSession = AVAssetExportSession(asset: asset, presetName: preset) else {
            throw IOSVideoNormalizationError.exportSessionUnavailable
        }

        guard exportSession.supportedFileTypes.contains(.mp4) else {
            throw IOSVideoNormalizationError.mp4OutputNotSupported
        }

        let outputUrl = makeOutputUrl()
        let fileManager = FileManager.default

        if fileManager.fileExists(atPath: outputUrl.path) {
            try? fileManager.removeItem(at: outputUrl)
        }

        exportSession.outputURL = outputUrl
        exportSession.outputFileType = .mp4
        exportSession.shouldOptimizeForNetworkUse = true
        exportSession.metadata = []

        try await export(exportSession)

        return outputUrl
    }

    func durationMillis(for url: URL) -> Int64? {
        let asset = AVURLAsset(url: url)
        let seconds = CMTimeGetSeconds(asset.duration)

        guard seconds.isFinite, !seconds.isNaN, seconds > 0 else {
            return nil
        }

        return Int64(seconds * 1000.0)
    }

    private func export(_ exportSession: AVAssetExportSession) async throws {
        try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
            exportSession.exportAsynchronously {
                switch exportSession.status {
                case .completed:
                    continuation.resume()

                case .failed:
                    continuation.resume(
                        throwing: IOSVideoNormalizationError.exportFailed(
                            exportSession.error?.localizedDescription ?? "Errore sconosciuto"
                        )
                    )

                case .cancelled:
                    continuation.resume(
                        throwing: IOSVideoNormalizationError.cancelled
                    )

                default:
                    continuation.resume(
                        throwing: IOSVideoNormalizationError.exportFailed(
                            exportSession.error?.localizedDescription ?? "Stato export non valido"
                        )
                    )
                }
            }
        }
    }

    private func choosePreset(for asset: AVAsset) throws -> String {
        let compatible = AVAssetExportSession.exportPresets(compatibleWith: asset)

        let preferredPresets = [
            AVAssetExportPreset1920x1080,
            AVAssetExportPreset1280x720,
            AVAssetExportPreset960x540,
            AVAssetExportPreset640x480
        ]

        if let preset = preferredPresets.first(where: { compatible.contains($0) }) {
            return preset
        }

        if compatible.contains(AVAssetExportPresetMediumQuality) {
            return AVAssetExportPresetMediumQuality
        }

        if compatible.contains(AVAssetExportPresetHighestQuality) {
            return AVAssetExportPresetHighestQuality
        }

        throw IOSVideoNormalizationError.exportSessionUnavailable
    }

    private func makeOutputUrl() -> URL {
        return URL(fileURLWithPath: NSTemporaryDirectory())
            .appendingPathComponent("normalized_\(UUID().uuidString).mp4")
    }
}