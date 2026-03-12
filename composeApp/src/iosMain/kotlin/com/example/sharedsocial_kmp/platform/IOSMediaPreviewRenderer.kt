package com.example.sharedsocial_kmp.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.viewinterop.UIKitViewController
import com.example.sharedsocial_kmp.core.platform.MediaPreviewRenderer
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.play
import platform.AVKit.AVPlayerViewController
import platform.Foundation.NSURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UIViewContentMode

class IOSMediaPreviewRenderer : MediaPreviewRenderer {

    @Composable
    override fun Render(
        media: MediaAsset,
        modifier: Modifier,
    ) {
        when (media) {

            is MediaAsset.Photo -> {
                UIKitView(
                    factory = {
                        UIImageView().apply {
                            contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
                            clipsToBounds = true
                        }
                    },
                    modifier = modifier,
                    update = { imageView ->
                        val image = loadImage(media.localPath)
                        println("IOS preview image loaded: ${image != null}")
                        imageView.image = image
                    }
                )
            }

            is MediaAsset.Video -> {
                UIKitViewController(
                    modifier = modifier,
                    factory = {
                        AVPlayerViewController().apply {
                            val url = resolveUrl(media.localPath)
                            if (url != null) {
                                player = AVPlayer(uRL = url)
                                player?.play()
                            }
                            showsPlaybackControls = true
                        }
                    },
                    update = { }
                )
            }
        }
    }

    private fun loadImage(path: String): UIImage? {
        val url = resolveUrl(path) ?: return null
        val filePath = url.path ?: return null
        return UIImage.imageWithContentsOfFile(filePath)
    }

    private fun resolveUrl(path: String): NSURL? {
        return when {
            path.startsWith("file://") -> NSURL.URLWithString(path)
            path.startsWith("/") -> NSURL.fileURLWithPath(path)
            else -> NSURL.URLWithString(path)
        }
    }
}