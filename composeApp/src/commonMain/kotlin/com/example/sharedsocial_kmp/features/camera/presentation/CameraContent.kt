package com.example.sharedsocial_kmp.features.camera.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sharedsocial_kmp.features.camera.domain.model.CameraMode
import com.example.sharedsocial_kmp.features.camera.presentation.CameraEvent
import com.example.sharedsocial_kmp.features.camera.presentation.CameraState

@Composable
fun CameraContent(
    state: CameraState,
    preview: @Composable (Modifier) -> Unit,
    onEvent: (CameraEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        preview(
            Modifier
                .fillMaxSize()
                .zIndex(0f)
        )

        if (state.selectedMode == CameraMode.VIDEO) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
                    .zIndex(1f),
                color = Color.Black.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = formatElapsed(state.elapsedRecordingSeconds),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color.White
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.35f))
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .zIndex(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onEvent(CameraEvent.OnPickMediaClick) }) {
                Icon(
                    imageVector = Icons.Default.Collections,
                    contentDescription = "Apri galleria",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            if (state.selectedMode == CameraMode.PHOTO) {
                IconButton(onClick = { onEvent(CameraEvent.OnTakePhotoClick) }) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Scatta foto",
                        tint = Color.White,
                        modifier = Modifier.size(72.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        if (state.isRecording) {
                            onEvent(CameraEvent.OnStopRecordingClick)
                        } else {
                            onEvent(CameraEvent.OnStartRecordingClick)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (state.isRecording) {
                            Icons.Default.StopCircle
                        } else {
                            Icons.Default.RadioButtonChecked
                        },
                        contentDescription = "Registra video",
                        tint = if (state.isRecording) Color.Red else Color.White,
                        modifier = Modifier.size(72.dp)
                    )
                }
            }

            IconButton(onClick = { onEvent(CameraEvent.OnSwitchCameraClick) }) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Cambia camera",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

private fun formatElapsed(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    val h = hours.toString().padStart(2, '0')
    val m = minutes.toString().padStart(2, '0')
    val s = seconds.toString().padStart(2, '0')

    return "$h:$m:$s"
}