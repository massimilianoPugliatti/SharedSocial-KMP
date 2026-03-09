package com.example.sharedsocial_kmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import com.example.sharedsocial_kmp.core.service.CameraService
import com.example.sharedsocial_kmp.data.service.AndroidCameraFacade
import com.example.sharedsocial_kmp.data.service.AndroidMediaPickerService
import com.mmk.kmpnotifier.permission.permissionUtil
import org.koin.mp.KoinPlatform.getKoin

class MainActivity : ComponentActivity() {
    private val permissionUtil by permissionUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val koin = getKoin()

        val cameraFacade = koin.get<CameraService>() as AndroidCameraFacade
        cameraFacade.bindLifecycle(this)

        val mediaPickerService = koin.get<AndroidMediaPickerService>()
        mediaPickerService.updateContext(this)

        setContent {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                mediaPickerService.onActivityResult(result)
            }

            LaunchedEffect(launcher) {
                mediaPickerService.attachLauncher(launcher)
            }
            App()
        }
    }
}