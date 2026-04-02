package com.example.sharedsocial_kmp.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.example.sharedsocial_kmp.core.platform.MediaPreviewRenderer
import com.example.sharedsocial_kmp.core.platform.MediaRenderScaleMode
import com.example.sharedsocial_kmp.core.platform.VideoPlaybackUiState
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import kotlinx.coroutines.delay
import platform.AVFoundation.AVLayerVideoGravityResizeAspect
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.currentItem
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.rate
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSData
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIColor
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UIView
import platform.UIKit.UIViewContentMode
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class)
class IOSMediaPreviewRenderer : MediaPreviewRenderer {

    @Composable
    override fun Render(
        media: MediaAsset,
        modifier: Modifier,
        isActive: Boolean,
        replayToken: Int,
        scaleMode: MediaRenderScaleMode,
        onVideoUiStateChanged: (VideoPlaybackUiState) -> Unit,
    ) {
        val latestCallback = rememberUpdatedState(onVideoUiStateChanged)

        when (media) {
            is MediaAsset.Photo -> {
                LaunchedEffect(media.localPath) {
                    latestCallback.value(VideoPlaybackUiState())
                }

                UIKitView(
                    modifier = modifier,
                    factory = {
                        UIImageView().apply {
                            contentMode = when (scaleMode) {
                                MediaRenderScaleMode.Fill -> UIViewContentMode.UIViewContentModeScaleAspectFill
                                MediaRenderScaleMode.Fit -> UIViewContentMode.UIViewContentModeScaleAspectFit
                            }
                            clipsToBounds = true
                            backgroundColor = UIColor.blackColor
                        }
                    },
                    update = { imageView ->
                        imageView.contentMode = when (scaleMode) {
                            MediaRenderScaleMode.Fill -> UIViewContentMode.UIViewContentModeScaleAspectFill
                            MediaRenderScaleMode.Fit -> UIViewContentMode.UIViewContentModeScaleAspectFit
                        }
                        loadImage(media.localPath, imageView)
                    }
                )
            }

            is MediaAsset.Video -> {
                val playerKey = remember(media.localPath, replayToken) {
                    "${media.localPath}#$replayToken"
                }

                val url = remember(playerKey) {
                    resolveUrl(media.localPath)
                }

                val player = remember(playerKey) {
                    url?.let { AVPlayer(uRL = it) }
                }

                val playerLayer = remember(playerKey) {
                    player?.let { AVPlayerLayer.playerLayerWithPlayer(it) }
                }

                val gravity = when (scaleMode) {
                    MediaRenderScaleMode.Fill -> AVLayerVideoGravityResizeAspectFill
                    MediaRenderScaleMode.Fit -> AVLayerVideoGravityResizeAspect
                }

                val hostView = remember {
                    IOSVideoPlayerHostView().apply {
                        backgroundColor = UIColor.blackColor
                    }
                }

                var ended by remember(playerKey) { mutableStateOf(false) }

                DisposableEffect(playerKey) {
                    latestCallback.value(
                        VideoPlaybackUiState(
                            isLoading = true,
                            canReplay = false
                        )
                    )

                    val observer = player?.currentItem?.let { item ->
                        NSNotificationCenter.defaultCenter.addObserverForName(
                            name = AVPlayerItemDidPlayToEndTimeNotification,
                            `object` = item,
                            queue = null
                        ) { _ ->
                            ended = true
                            latestCallback.value(
                                VideoPlaybackUiState(
                                    isLoading = false,
                                    canReplay = true
                                )
                            )
                        }
                    }

                    onDispose {
                        observer?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
                        player?.pause()
                        playerLayer?.player = null

                        if (hostView.playerLayer === playerLayer) {
                            hostView.playerLayer = null
                        }
                    }
                }

                LaunchedEffect(playerKey, isActive) {
                    if (player == null) return@LaunchedEffect

                    if (!isActive) {
                        player.pause()
                        latestCallback.value(
                            VideoPlaybackUiState(
                                isLoading = false,
                                canReplay = ended
                            )
                        )
                    } else {
                        if (!ended) {
                            latestCallback.value(
                                VideoPlaybackUiState(
                                    isLoading = true,
                                    canReplay = false
                                )
                            )
                            player.play()
                        }
                    }
                }

                LaunchedEffect(playerKey, isActive) {
                    if (player == null || !isActive) return@LaunchedEffect

                    repeat(50) {
                        if (ended) return@LaunchedEffect

                        val isPlaying = player.rate > 0.0
                        latestCallback.value(
                            VideoPlaybackUiState(
                                isLoading = !isPlaying,
                                canReplay = false
                            )
                        )

                        if (isPlaying) return@LaunchedEffect
                        delay(200)
                    }
                }

                UIKitView(
                    modifier = modifier,
                    factory = {
                        hostView
                    },
                    update = { view ->
                        playerLayer?.videoGravity = gravity
                        view.playerLayer = playerLayer
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

@OptIn(ExperimentalForeignApi::class)
private class IOSVideoPlayerHostView(
    frame: CValue<CGRect> = CGRectZero.readValue()
) : UIView(frame) {

    var playerLayer: AVPlayerLayer? = null
        set(value) {
            if (field === value) return
            field?.removeFromSuperlayer()
            field = value
            value?.let { layer.addSublayer(it) }
            setNeedsLayout()
            layoutIfNeeded()
        }

    override fun layoutSubviews() {
        super.layoutSubviews()
        playerLayer?.frame = bounds
    }
}