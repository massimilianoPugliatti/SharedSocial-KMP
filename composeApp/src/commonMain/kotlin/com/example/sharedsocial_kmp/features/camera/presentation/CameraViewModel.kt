package com.example.sharedsocial_kmp.features.camera.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.core.navigation.AppNavigator
import com.example.sharedsocial_kmp.core.service.CameraPermissionService
import com.example.sharedsocial_kmp.core.service.CameraService
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraError
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraMode
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult
import com.example.sharedsocial_kmp.features.camera.domain.usecase.CapturePhotoUseCase
import com.example.sharedsocial_kmp.features.camera.domain.usecase.PickMediaUseCase
import com.example.sharedsocial_kmp.features.camera.domain.usecase.StartVideoRecordingUseCase
import com.example.sharedsocial_kmp.features.camera.domain.usecase.StopVideoRecordingUseCase
import com.example.sharedsocial_kmp.features.camera.domain.usecase.SwitchCameraUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(
    private val navigator: AppNavigator,
    private val cameraService: CameraService,
    private val permissionService: CameraPermissionService,
    private val capturePhotoUseCase: CapturePhotoUseCase,
    private val startVideoRecordingUseCase: StartVideoRecordingUseCase,
    private val stopVideoRecordingUseCase: StopVideoRecordingUseCase,
    private val switchCameraUseCase: SwitchCameraUseCase,
    private val pickMediaUseCase: PickMediaUseCase,
    private val dispatchers: AppDispatchers
) : ScreenModel {

    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    private var recordingTimerJob: Job? = null

    fun onEvent(event: CameraEvent) {
        when (event) {
            CameraEvent.OnStart -> startCamera()
            CameraEvent.OnStop -> stopCamera()
            CameraEvent.OnBackClick -> navigator.goBack()
            CameraEvent.OnTakePhotoClick -> takePhoto()
            CameraEvent.OnStartRecordingClick -> startRecording()
            CameraEvent.OnStopRecordingClick -> stopRecording()
            CameraEvent.OnSwitchCameraClick -> switchCamera()
            CameraEvent.OnPickMediaClick -> pickMedia()
            is CameraEvent.OnModeChanged -> {
                _state.update { it.copy(selectedMode = event.mode) }
            }
            CameraEvent.OnMessageConsumed -> {
                _state.update { it.copy(uiMessage = null) }
            }
            CameraEvent.OnCapturedMediaConsumed -> {
                _state.update { it.copy(capturedMedia = null) }
            }
        }
    }

    private fun startCamera() {
        screenModelScope.launch(dispatchers.main) {
            val cameraPermission = permissionService.ensureCameraPermission()
            if (cameraPermission is CameraResult.Failure) {
                publishError(cameraPermission.error)
                return@launch
            }

            if (_state.value.selectedMode == CameraMode.VIDEO) {
                val micPermission = permissionService.ensureMicrophonePermission()
                if (micPermission is CameraResult.Failure) {
                    publishError(micPermission.error)
                    return@launch
                }
            }

            when (val result = cameraService.start()) {
                is CameraResult.Success -> Unit
                is CameraResult.Failure -> publishError(result.error)
            }
        }
    }

    private fun stopCamera() {
        screenModelScope.launch(dispatchers.main) {
            recordingTimerJob?.cancel()
            cameraService.stop()
        }
    }

    private fun takePhoto() {
        screenModelScope.launch(dispatchers.main) {
            _state.update { it.copy(isLoading = true) }
            when (val result = capturePhotoUseCase()) {
                is CameraResult.Success -> {
                    println("result: ${result.value}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            capturedMedia = result.value
                        )
                    }
                }
                is CameraResult.Failure -> {
                    _state.update { it.copy(isLoading = false) }
                    publishError(result.error)
                }
            }
        }
    }

    private fun startRecording() {
        screenModelScope.launch(dispatchers.main) {
            when (val result = startVideoRecordingUseCase()) {
                is CameraResult.Success -> {
                    _state.update {
                        it.copy(
                            isRecording = true,
                            elapsedRecordingSeconds = 0L
                        )
                    }
                    startTimer()
                }
                is CameraResult.Failure -> publishError(result.error)
            }
        }
    }

    private fun stopRecording() {
        screenModelScope.launch(dispatchers.main) {
            when (val result = stopVideoRecordingUseCase()) {
                is CameraResult.Success -> {
                    recordingTimerJob?.cancel()
                    println("result: ${result.value}")
                    _state.update {
                        it.copy(
                            isRecording = false,
                            elapsedRecordingSeconds = 0L,
                            capturedMedia = result.value
                        )
                    }
                }
                is CameraResult.Failure -> publishError(result.error)
            }
        }
    }

    private fun switchCamera() {
        screenModelScope.launch(dispatchers.main) {
            when (val result = switchCameraUseCase()) {
                is CameraResult.Success -> Unit
                is CameraResult.Failure -> publishError(result.error)
            }
        }
    }

    private fun pickMedia() {
        screenModelScope.launch(dispatchers.main) {
            when (val result = pickMediaUseCase()) {
                is CameraResult.Success -> {
                    println("result: ${result.value}")
                    _state.update { it.copy(capturedMedia = result.value) }
                }
                is CameraResult.Failure -> publishError(result.error)
            }
        }
    }

    private fun startTimer() {
        recordingTimerJob?.cancel()
        recordingTimerJob = screenModelScope.launch(dispatchers.main) {
            while (true) {
                delay(1_000)
                _state.update {
                    it.copy(elapsedRecordingSeconds = it.elapsedRecordingSeconds + 1)
                }
            }
        }
    }

    private fun publishError(error: CameraError) {
        val message = when (error) {
            CameraError.PermissionDenied -> CameraUiMessage.PermissionDenied
            CameraError.CameraUnavailable -> CameraUiMessage.CameraUnavailable
            CameraError.CaptureFailed -> CameraUiMessage.CaptureFailed
            CameraError.RecordingAlreadyStarted -> CameraUiMessage.RecordingAlreadyStarted
            CameraError.RecordingNotStarted -> CameraUiMessage.RecordingNotStarted
            CameraError.MicrophonePermissionDenied -> CameraUiMessage.Generic("Permesso microfono negato")
            CameraError.PickerCancelled -> CameraUiMessage.Generic("Selezione annullata")
            is CameraError.Unknown -> CameraUiMessage.Generic(error.message ?: "Errore sconosciuto")
        }
        _state.update { it.copy(uiMessage = message) }
    }

    override fun onDispose() {
        recordingTimerJob?.cancel()
        screenModelScope.launch(dispatchers.main) {
            cameraService.stop()
        }
        super.onDispose()
    }
}