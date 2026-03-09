package com.example.sharedsocial_kmp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.LifecycleOwner
import com.example.sharedsocial_kmp.data.service.AndroidMediaPickerService
import com.mmk.kmpnotifier.permission.permissionUtil
import org.koin.compose.koinInject
import org.koin.mp.KoinPlatform.getKoin

class MainActivity : ComponentActivity() {
    private val permissionUtil by permissionUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val koin = getKoin()
        koin.declare(this as LifecycleOwner)
        koin.declare(this as Activity)

        setContent {
            val mediaPickerService = koinInject<AndroidMediaPickerService>()

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