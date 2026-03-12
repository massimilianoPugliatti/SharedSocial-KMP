package com.example.sharedsocial_kmp.features.camera.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.core.navigation.AppNavigator
import com.example.sharedsocial_kmp.core.platform.CameraPermissionRequester
import com.example.sharedsocial_kmp.core.platform.CameraPermissionService
import com.example.sharedsocial_kmp.core.platform.CameraService
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
    private val permissionRequester: CameraPermissionRequester,
    private val capturePhotoUseCase: CapturePhotoUseCase,
    private val startVideoRecordingUseCase: StartVideoRecordingUseCase,
    private val stopVideoRecordingUseCase: StopVideoRecordingUseCase,
    private val switchCameraUseCase: SwitchCameraUseCase,
    private val pickMediaUseCase: PickMediaUseCase,
    private val dispatchers: AppDispatchers,
) : ScreenModel {

    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    private var recordingTimerJob: Job? = null
    private var isCameraStarted = false

    fun onEvent(event: CameraEvent) {
        when (event) {
            CameraEvent.OnStart -> checkPermissionsAndStart()
            CameraEvent.OnStop -> stopCamera()
            CameraEvent.OnBackClick -> navigator.goBack()
            CameraEvent.OnTakePhotoClick -> takePhoto()
            CameraEvent.OnStartRecordingClick -> startRecording()
            CameraEvent.OnStopRecordingClick -> stopRecording()
            CameraEvent.OnSwitchCameraClick -> switchCamera()
            CameraEvent.OnPickMediaClick -> pickMedia()

            is CameraEvent.OnModeChanged -> {
                _state.update { current ->
                    if (current.selectedMode == event.mode) current
                    else current.copy(selectedMode = event.mode)
                }
            }

            CameraEvent.OnMessageConsumed -> {
                _state.update { it.copy(uiMessage = null) }
            }

            CameraEvent.OnCapturedMediaConsumed -> {
                _state.update { it.copy(capturedMedia = null) }
            }
        }
    }

    private fun checkPermissionsAndStart() {
        if (isCameraStarted) return

        screenModelScope.launch(dispatchers.main) {
            val needsMicrophone = _state.value.selectedMode == CameraMode.VIDEO

            val cameraGranted =
                permissionService.ensureCameraPermission() is CameraResult.Success

            val microphoneGranted =
                if (needsMicrophone) {
                    permissionService.ensureMicrophonePermission() is CameraResult.Success
                } else {
                    true
                }

            if (cameraGranted && microphoneGranted) {
                startCameraInternal()
                return@launch
            }

            val requestResult = permissionRequester.requestPermissions(
                needsMicrophone = needsMicrophone
            )

            when {
                !requestResult.cameraGranted -> publishError(CameraError.PermissionDenied)
                needsMicrophone && !requestResult.microphoneGranted ->
                    publishError(CameraError.MicrophonePermissionDenied)

                else -> startCameraInternal()
            }
        }
    }

    private fun startCameraInternal() {
        if (isCameraStarted) return

        screenModelScope.launch(dispatchers.main) {
            when (val result = cameraService.start()) {
                is CameraResult.Success -> {
                    isCameraStarted = true
                }

                is CameraResult.Failure -> {
                    publishError(result.error)
                }
            }
        }
    }

    private fun stopCamera() {
        screenModelScope.launch(dispatchers.main) {
            recordingTimerJob?.cancel()
            recordingTimerJob = null
            _state.update {
                it.copy(
                    isRecording = false,
                    elapsedRecordingSeconds = 0L
                )
            }
            if (isCameraStarted) {
                cameraService.stop()
                isCameraStarted = false
            }
        }
    }

    private fun takePhoto() {
        screenModelScope.launch(dispatchers.main) {
            _state.update { it.copy(isLoading = true) }
            when (val result = capturePhotoUseCase()) {
                is CameraResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    navigator.navigateToCreatePost(result.value)
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
                    recordingTimerJob = null

                    _state.update {
                        it.copy(
                            isRecording = false,
                            elapsedRecordingSeconds = 0L
                        )
                    }
                    navigator.navigateToCreatePost(result.value)
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
                is CameraResult.Success -> navigator.navigateToCreatePost(result.value)
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
        _state.update {
            it.copy(uiMessage = CameraErrorUiResolver.resolve(error))
        }
    }

    override fun onDispose() {
        recordingTimerJob?.cancel()
        recordingTimerJob = null

        screenModelScope.launch(dispatchers.main) {
            cameraService.stop()
            isCameraStarted = false
        }

        super.onDispose()
    }
}