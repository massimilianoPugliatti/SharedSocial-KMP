//
//  IOSCameraEngine.swift
//  iosApp
//
//  Created by Massimiliano Pugliatti on 09/03/26.
//

import AVFoundation
import Foundation
import UIKit

final class IOSCameraPreviewViewController: UIViewController {
    private let previewLayer: AVCaptureVideoPreviewLayer

    init(session: AVCaptureSession) {
        self.previewLayer = AVCaptureVideoPreviewLayer(session: session)
        super.init(nibName: nil, bundle: nil)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .black

        previewLayer.videoGravity = .resizeAspect
        previewLayer.needsDisplayOnBoundsChange = true

        view.layer.addSublayer(previewLayer)
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        applyPreviewFrame()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        applyPreviewFrame()

        DispatchQueue.main.async { [weak self] in
            self?.applyPreviewFrame()
        }
    }

    override func viewSafeAreaInsetsDidChange() {
        super.viewSafeAreaInsetsDidChange()
        applyPreviewFrame()
    }

    private func applyPreviewFrame() {
        CATransaction.begin()
        CATransaction.setDisableActions(true)
        previewLayer.frame = view.bounds
        CATransaction.commit()
    }
}

final class IOSCameraEngine: NSObject, AVCapturePhotoCaptureDelegate, AVCaptureFileOutputRecordingDelegate {

    private let session = AVCaptureSession()
    private let sessionQueue = DispatchQueue(label: "camera.session.queue")

    private let movieOutput = AVCaptureMovieFileOutput()
    private let photoOutput = AVCapturePhotoOutput()

    private var currentPosition: AVCaptureDevice.Position = .back
    private var isConfigured = false
    private var isConfiguring = false

    private lazy var previewController = IOSCameraPreviewViewController(session: session)

    private var onPhotoCaptured: ((Result<String, IOSCameraEngineError>) -> Void)?
    private var onVideoCaptured: ((Result<String, IOSCameraEngineError>) -> Void)?

    func makePreviewController() -> UIViewController {
        return previewController
    }

    func configureSessionIfNeeded() {
        sessionQueue.async {
            guard !self.isConfigured, !self.isConfiguring else { return }
            self.isConfiguring = true

            self.session.beginConfiguration()
            defer {
                self.session.commitConfiguration()
                self.isConfiguring = false
            }

            self.session.sessionPreset = .high

            do {
                try self.addInputs(position: self.currentPosition)

                if self.session.canAddOutput(self.photoOutput) {
                    self.session.addOutput(self.photoOutput)
                }

                if self.session.canAddOutput(self.movieOutput) {
                    self.session.addOutput(self.movieOutput)
                }

                self.isConfigured = true
            } catch {
                self.isConfigured = false
                print("Errore configurazione camera: \(error)")
            }
        }
    }

    private func addInputs(position: AVCaptureDevice.Position) throws {
        session.inputs.forEach { session.removeInput($0) }

        guard let videoDevice = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: position) else {
            throw IOSCameraEngineError.cameraUnavailable
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

    func startSessionIfNeeded() {
        sessionQueue.async {
            if !self.isConfigured && !self.isConfiguring {
                self.configureSessionIfNeeded()
            }

            self.sessionQueue.async {
                guard self.isConfigured else { return }
                guard !self.session.isRunning else { return }
                self.session.startRunning()
            }
        }
    }

    func stopSession() {
        sessionQueue.async {
            guard self.session.isRunning else { return }
            self.session.stopRunning()
        }
    }

    func switchCamera() {
        sessionQueue.async {
            self.session.beginConfiguration()
            defer { self.session.commitConfiguration() }

            self.currentPosition = (self.currentPosition == .back) ? .front : .back

            do {
                try self.addInputs(position: self.currentPosition)
            } catch {
                print("Errore switch camera: \(error)")
            }
        }
    }

    func capturePhoto(onComplete: @escaping (Result<String, IOSCameraEngineError>) -> Void) {
        guard isConfigured else {
            onComplete(.failure(.notConfigured))
            return
        }

        onPhotoCaptured = onComplete
        let settings = AVCapturePhotoSettings()
        photoOutput.capturePhoto(with: settings, delegate: self)
    }

    func startRecording() -> IOSCameraEngineError? {
        guard isConfigured else {
            return .notConfigured
        }

        if movieOutput.isRecording {
            return .recordingAlreadyStarted
        }

        sessionQueue.async {
            guard !self.movieOutput.isRecording else { return }

            let path = NSTemporaryDirectory() + "\(Date().timeIntervalSince1970).mp4"
            let url = URL(fileURLWithPath: path)

            self.movieOutput.startRecording(to: url, recordingDelegate: self)
        }

        return nil
    }

    func stopRecording(onComplete: @escaping (Result<String, IOSCameraEngineError>) -> Void) {
        guard isConfigured else {
            onComplete(.failure(.notConfigured))
            return
        }

        onVideoCaptured = onComplete

        sessionQueue.async {
            guard self.movieOutput.isRecording else {
                DispatchQueue.main.async {
                    onComplete(.failure(.recordingNotStarted))
                }
                return
            }

            self.movieOutput.stopRecording()
        }
    }

    func photoOutput(
        _ output: AVCapturePhotoOutput,
        didFinishProcessingPhoto photo: AVCapturePhoto,
        error: Error?
    ) {
        guard error == nil else {
            onPhotoCaptured?(.failure(.captureFailed))
            onPhotoCaptured = nil
            return
        }

        guard let data = photo.fileDataRepresentation() else {
            onPhotoCaptured?(.failure(.captureFailed))
            onPhotoCaptured = nil
            return
        }

        let path = NSTemporaryDirectory() + "\(Date().timeIntervalSince1970).jpg"
        let url = URL(fileURLWithPath: path)

        do {
            try data.write(to: url)
            onPhotoCaptured?(.success(url.path))
        } catch {
            onPhotoCaptured?(.failure(.unknown(error.localizedDescription)))
        }

        onPhotoCaptured = nil
    }

    func fileOutput(
        _ output: AVCaptureFileOutput,
        didFinishRecordingTo outputFileURL: URL,
        from connections: [AVCaptureConnection],
        error: Error?
    ) {
        guard error == nil else {
            onVideoCaptured?(.failure(.captureFailed))
            onVideoCaptured = nil
            return
        }

        onVideoCaptured?(.success(outputFileURL.path))
        onVideoCaptured = nil
    }
}
