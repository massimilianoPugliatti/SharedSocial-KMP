package com.example.sharedsocial_kmp.data.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.view.OrientationEventListener
import android.view.Surface
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.sharedsocial_kmp.core.service.CameraPermissionService
import com.example.sharedsocial_kmp.core.service.CameraPreviewRenderer
import com.example.sharedsocial_kmp.core.service.CameraService
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraError
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraResult
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume

class AndroidCameraFacade(
    private val context: Context
) : CameraService, CameraPreviewRenderer, CameraPermissionService {

    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var previewView: PreviewView? = null
    private var lifecycleOwner: LifecycleOwner? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null
    private var currentRotation: Int = Surface.ROTATION_0
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var currentVideoFile: File? = null

    private val orientationListener = object : OrientationEventListener(context) {
        override fun onOrientationChanged(orientation: Int) {
            if (orientation == ORIENTATION_UNKNOWN) return
            currentRotation = when (orientation) {
                in 45..134 -> Surface.ROTATION_270
                in 135..224 -> Surface.ROTATION_180
                in 225..314 -> Surface.ROTATION_90
                else -> Surface.ROTATION_0
            }
            imageCapture?.targetRotation = currentRotation
            videoCapture?.targetRotation = currentRotation
        }
    }

    fun bindLifecycle(owner: LifecycleOwner) {
        this.lifecycleOwner = owner
    }

    @Composable
    override fun Render(modifier: Modifier) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FIT_CENTER
                    previewView = this
                }
            },
            modifier = modifier,
            onRelease = {
                previewView = null
            }
        )
    }

    override suspend fun ensureCameraPermission(): CameraResult<Unit> {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        return if (granted) CameraResult.Success(Unit) else CameraResult.Failure(CameraError.PermissionDenied)
    }

    override suspend fun ensureMicrophonePermission(): CameraResult<Unit> {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        return if (granted) CameraResult.Success(Unit) else CameraResult.Failure(CameraError.MicrophonePermissionDenied)
    }

    override suspend fun start(): CameraResult<Unit> {
        val pv = previewView ?: return CameraResult.Failure(CameraError.CameraUnavailable)
        val lifecycle = lifecycleOwner ?: return CameraResult.Failure(CameraError.Unknown("Lifecycle non collegato"))

        orientationListener.enable()

        return suspendCancellableCoroutine { cont ->
            val providerFuture = ProcessCameraProvider.getInstance(context)
            providerFuture.addListener({
                try {
                    val provider = providerFuture.get()

                    val preview = Preview.Builder()
                        .setTargetRotation(currentRotation)
                        .build()
                        .also { it.setSurfaceProvider(pv.surfaceProvider) }

                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(currentRotation)
                        .build()

                    val recorder = Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.SD))
                        .build()

                    videoCapture = VideoCapture.withOutput(recorder).apply {
                        targetRotation = currentRotation
                    }

                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycle,
                        cameraSelector,
                        preview,
                        imageCapture,
                        videoCapture
                    )
                    cont.resume(CameraResult.Success(Unit))
                } catch (t: Throwable) {
                    cont.resume(CameraResult.Failure(CameraError.Unknown(t.message)))
                }
            }, ContextCompat.getMainExecutor(context))
        }
    }

    override suspend fun stop(): CameraResult<Unit> {
        orientationListener.disable()
        activeRecording?.stop()
        activeRecording = null

        try {
            val provider = ProcessCameraProvider.getInstance(context).get()
            provider.unbindAll()
        } catch (e: Exception) {
            return CameraResult.Failure(CameraError.Unknown(e.message))
        }

        return CameraResult.Success(Unit)
    }

    override suspend fun capturePhoto(): CameraResult<MediaAsset.Photo> {
        val capture = imageCapture ?: return CameraResult.Failure(CameraError.CameraUnavailable)
        val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
        val output = ImageCapture.OutputFileOptions.Builder(file).build()

        return suspendCancellableCoroutine { cont ->
            capture.takePicture(
                output,
                cameraExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        cont.resume(CameraResult.Success(MediaAsset.Photo(localPath = file.absolutePath)))
                    }

                    override fun onError(exception: ImageCaptureException) {
                        cont.resume(CameraResult.Failure(CameraError.CaptureFailed))
                    }
                }
            )
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun startVideoRecording(): CameraResult<Unit> {
        if (activeRecording != null) return CameraResult.Failure(CameraError.RecordingAlreadyStarted)

        val capture = videoCapture ?: return CameraResult.Failure(CameraError.CameraUnavailable)
        val file = File(context.cacheDir, "video_${System.currentTimeMillis()}.mp4")
        currentVideoFile = file

        return suspendCancellableCoroutine { cont ->
            activeRecording = capture.output
                .prepareRecording(context, FileOutputOptions.Builder(file).build())
                .withAudioEnabled()
                .start(cameraExecutor) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> cont.resume(CameraResult.Success(Unit))
                        is VideoRecordEvent.Finalize -> {
                            if (event.hasError()) currentVideoFile = null
                        }
                    }
                }
        }
    }

    override suspend fun stopVideoRecording(): CameraResult<MediaAsset.Video> {
        val recording = activeRecording ?: return CameraResult.Failure(CameraError.RecordingNotStarted)
        val file = currentVideoFile ?: return CameraResult.Failure(CameraError.CaptureFailed)

        recording.stop()
        activeRecording = null
        currentVideoFile = null

        return CameraResult.Success(MediaAsset.Video(localPath = file.absolutePath))
    }

    override suspend fun switchCamera(): CameraResult<Unit> {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        return start()
    }
}