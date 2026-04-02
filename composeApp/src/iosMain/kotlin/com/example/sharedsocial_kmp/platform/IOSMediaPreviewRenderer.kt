package com.example.sharedsocial_kmp.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.viewinterop.UIKitViewController
import com.example.sharedsocial_kmp.core.platform.MediaPreviewRenderer
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVKit.AVPlayerViewController
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UIViewContentMode
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue

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
                        loadImage(media.localPath, imageView)
                    }
                )
            }

            is MediaAsset.Video -> {
                UIKitViewController(
                    modifier = modifier,
                    factory = {
                        AVPlayerViewController().apply {
                            showsPlaybackControls = false
                            val url = resolveUrl(media.localPath)
                            if (url != null) {
                                player = AVPlayer(uRL = url)
                                player?.play()
                            }
                        }
                    },
                    update = { controller ->
                        val url = resolveUrl(media.localPath)
                        if (url != null) {
                            controller.player?.pause()
                            controller.player = AVPlayer(uRL = url)
                            controller.player?.play()
                        }
                    }
                )
            }
        }
    }

    private fun loadImage(path: String, imageView: UIImageView) {
        val url = resolveUrl(path) ?: run {
            imageView.image = null
            return
        }

        if (path.startsWith("http://") || path.startsWith("https://")) {
            val marker = path.hashCode().toLong()
            imageView.tag = marker

            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0u)) {
                val data = NSData.dataWithContentsOfURL(url)
                val image = data?.let { UIImage(data = it) }

                dispatch_async(dispatch_get_main_queue()) {
                    if (imageView.tag == marker) {
                        imageView.image = image
                    }
                }
            }
        } else {
            val filePath = url.path ?: return
            imageView.image = UIImage.imageWithContentsOfFile(filePath)
        }
    }

    private fun resolveUrl(path: String): NSURL? {
        return when {
            path.startsWith("file://") -> NSURL.URLWithString(path)
            path.startsWith("/") -> NSURL.fileURLWithPath(path)
            path.startsWith("http://") -> NSURL.URLWithString(path)
            path.startsWith("https://") -> NSURL.URLWithString(path)
            else -> NSURL.URLWithString(path)
        }
    }
}