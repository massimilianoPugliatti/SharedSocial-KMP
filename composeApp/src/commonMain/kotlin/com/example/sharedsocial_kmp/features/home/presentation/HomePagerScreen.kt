package com.example.sharedsocial_kmp.features.home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.sharedsocial_kmp.core.platform.CameraPreviewRenderer
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraMode
import com.example.sharedsocial_kmp.features.camera.presentation.CameraEvent
import com.example.sharedsocial_kmp.features.camera.presentation.CameraViewModel
import com.example.sharedsocial_kmp.features.camera.presentation.components.CameraControlsContent
import com.example.sharedsocial_kmp.features.camera.presentation.components.CameraPreviewHost
import com.example.sharedsocial_kmp.features.feed.presentation.FeedScreen
import org.koin.compose.koinInject

class HomePagerScreen : Screen {

    @Composable
    override fun Content() {
        val pagerState = rememberPagerState(
            initialPage = 1,
            pageCount = { 3 }
        )

        val cameraViewModel: CameraViewModel = koinScreenModel()
        val previewRenderer: CameraPreviewRenderer = koinInject()
        val cameraState by cameraViewModel.state.collectAsState()
        val feedScreen = remember { FeedScreen() }

        val activePage by remember {
            derivedStateOf {
                if (pagerState.targetPage != pagerState.currentPage) {
                    pagerState.targetPage
                } else {
                    pagerState.currentPage
                }
            }
        }

        val isCameraVisible by remember {
            derivedStateOf { activePage == 0 || activePage == 2 }
        }

        val activeMode by remember {
            derivedStateOf {
                when (activePage) {
                    2 -> CameraMode.VIDEO
                    else -> CameraMode.PHOTO
                }
            }
        }

        LaunchedEffect(activePage) {
            when (activePage) {
                0 -> {
                    cameraViewModel.onEvent(CameraEvent.OnModeChanged(CameraMode.PHOTO))
                    cameraViewModel.onEvent(CameraEvent.OnStart)
                }

                1 -> {
                    cameraViewModel.onEvent(CameraEvent.OnStop)
                }

                2 -> {
                    cameraViewModel.onEvent(CameraEvent.OnModeChanged(CameraMode.VIDEO))
                    cameraViewModel.onEvent(CameraEvent.OnStart)
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CameraPreviewHost(
                previewRenderer = previewRenderer,
                isVisible = isCameraVisible,
                modifier = Modifier.fillMaxSize()
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> {
                        CameraControlsContent(
                            mode = CameraMode.PHOTO,
                            isLoading = cameraState.isLoading,
                            isRecording = cameraState.isRecording && activeMode == CameraMode.VIDEO,
                            elapsedRecordingSeconds = cameraState.elapsedRecordingSeconds,
                            onEvent = cameraViewModel::onEvent,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    1 -> {
                        feedScreen.Content()
                    }

                    2 -> {
                        CameraControlsContent(
                            mode = CameraMode.VIDEO,
                            isLoading = cameraState.isLoading,
                            isRecording = cameraState.isRecording,
                            elapsedRecordingSeconds = cameraState.elapsedRecordingSeconds,
                            onEvent = cameraViewModel::onEvent,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}