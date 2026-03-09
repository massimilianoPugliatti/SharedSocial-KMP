//
//  IOSCameraEngine.swift
//  iosApp
//
//  Created by Massimiliano Pugliatti on 09/03/26.
//

import Foundation
import UIKit
import AVFoundation

final class IOSCameraEngine: NSObject, AVCapturePhotoCaptureDelegate, AVCaptureFileOutputRecordingDelegate {

    private let session = AVCaptureSession()
    private let sessionQueue = DispatchQueue(label: "camera.session.queue")

    private let movieOutput = AVCaptureMovieFileOutput()
    private let photoOutput = AVCapturePhotoOutput()

    private var currentPosition: AVCaptureDevice.Position = .back
    private var isConfigured = false

    private var onPhotoCaptured: ((String?) -> Void)?
    private var onVideoCaptured: ((String?) -> Void)?

    func configureSessionIfNeeded() {
        sessionQueue.sync {
            guard !isConfigured else { return }

            session.beginConfiguration()
            defer {
                session.commitConfiguration()
            }

            session.sessionPreset = .high

            do {
                try addInputs(position: currentPosition)

                if session.canAddOutput(photoOutput) {
                    session.addOutput(photoOutput)
                }

                if session.canAddOutput(movieOutput) {
                    session.addOutput(movieOutput)
                }

                isConfigured = true
            } catch {
                print("Errore configurazione camera: \(error)")
            }
        }
    }

    private func addInputs(position: AVCaptureDevice.Position) throws {
        session.inputs.forEach { session.removeInput($0) }

        guard let videoDevice = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: position) else {
            throw NSError(domain: "camera", code: 1, userInfo: [NSLocalizedDescriptionKey: "Video device non disponibile"])
        }

        let videoInput = try AVCaptureDeviceInput(device: videoDevice)
        if session.canAddInput(videoInput) {
            session.addInput(videoInput)
        }

        if let audioDevice = AVCaptureDevice.default(for: .audio) {
            let audioInput = try AVCaptureDeviceInput(device: audioDevice)
            if session.canAddInput(audioInput) {
                session.addInput(audioInput)
            }
        }
    }

    func makePreviewController() -> UIViewController {
        configureSessionIfNeeded()

        let vc = UIViewController()
        vc.view.backgroundColor = .black

        let previewLayer = AVCaptureVideoPreviewLayer(session: session)
        previewLayer.videoGravity = .resizeAspectFill
        previewLayer.frame = vc.view.bounds
        vc.view.layer.addSublayer(previewLayer)

        DispatchQueue.main.async {
            previewLayer.frame = vc.view.bounds
        }

        startSessionIfNeeded()

        return vc
    }

    func startSessionIfNeeded() {
        sessionQueue.async {
            guard self.isConfigured else { return }
            guard !self.session.isRunning else { return }
            self.session.startRunning()
        }
    }

    func stopSession() {
        sessionQueue.async {
            guard self.session.isRunning else { return }
            self.session.stopRunning()
        }
    }

    func switchCamera() throws {
        sessionQueue.sync {
            session.beginConfiguration()
            defer {
                session.commitConfiguration()
            }

            currentPosition = (currentPosition == .back) ? .front : .back

            do {
                try addInputs(position: currentPosition)
            } catch {
                print("Errore switch camera: \(error)")
            }
        }
    }

    func capturePhoto(onComplete: @escaping (String?) -> Void) {
        self.onPhotoCaptured = onComplete
        let settings = AVCapturePhotoSettings()
        photoOutput.capturePhoto(with: settings, delegate: self)
    }

    func startRecording() {
        sessionQueue.async {
            guard !self.movieOutput.isRecording else { return }

            let path = NSTemporaryDirectory() + "\(Date().timeIntervalSince1970).mp4"
            let url = URL(fileURLWithPath: path)

            self.movieOutput.startRecording(to: url, recordingDelegate: self)
        }
    }

    func stopRecording(onComplete: @escaping (String?) -> Void) {
        self.onVideoCaptured = onComplete
        sessionQueue.async {
            guard self.movieOutput.isRecording else {
                DispatchQueue.main.async {
                    onComplete(nil)
                }
                return
            }
            self.movieOutput.stopRecording()
        }
    }

    func photoOutput(_ output: AVCapturePhotoOutput,
                     didFinishProcessingPhoto photo: AVCapturePhoto,
                     error: Error?) {
        guard error == nil, let data = photo.fileDataRepresentation() else {
            onPhotoCaptured?(nil)
            return
        }

        let path = NSTemporaryDirectory() + "\(Date().timeIntervalSince1970).jpg"
        let url = URL(fileURLWithPath: path)

        do {
            try data.write(to: url)
            onPhotoCaptured?(url.path)
        } catch {
            onPhotoCaptured?(nil)
        }
    }

    func fileOutput(_ output: AVCaptureFileOutput,
                    didFinishRecordingTo outputFileURL: URL,
                    from connections: [AVCaptureConnection],
                    error: Error?) {
        guard error == nil else {
            onVideoCaptured?(nil)
            return
        }
        onVideoCaptured?(outputFileURL.path)
    }
}
