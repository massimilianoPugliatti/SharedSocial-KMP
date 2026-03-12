package com.example.sharedsocial_kmp.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import com.example.sharedsocial_kmp.core.platform.CameraPreviewRenderer
import platform.UIKit.UIViewController

class IOSCameraPreviewRenderer(
    private val controllerProvider: () -> UIViewController,
) : CameraPreviewRenderer {

    @Composable
    override fun Render(modifier: Modifier) {
        UIKitViewController(
            factory = { controllerProvider() },
            modifier = modifier,
            update = { controller ->
                controller.view.setNeedsLayout()
                controller.view.layoutIfNeeded()
            },
            properties = UIKitInteropProperties(
                isInteractive = true,
                isNativeAccessibilityEnabled = true
            )
        )
    }
}