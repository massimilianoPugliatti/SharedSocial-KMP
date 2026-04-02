package com.example.sharedsocial_kmp.platform

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.load
import com.example.sharedsocial_kmp.core.platform.MediaPreviewRenderer
import com.example.sharedsocial_kmp.core.platform.MediaRenderScaleMode
import com.example.sharedsocial_kmp.core.platform.VideoPlaybackUiState
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import java.io.File

class AndroidMediaPreviewRenderer(
    private val context: Context,
) : MediaPreviewRenderer {

    @OptIn(UnstableApi::class)
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

                AndroidView(
                    modifier = modifier,
                    factory = {
                        ImageView(context).apply {
                            scaleType = when (scaleMode) {
                                MediaRenderScaleMode.Fill -> ImageView.ScaleType.CENTER_CROP
                                MediaRenderScaleMode.Fit -> ImageView.ScaleType.FIT_CENTER
                            }
                        }
                    },
                    update = { imageView ->
                        imageView.scaleType = when (scaleMode) {
                            MediaRenderScaleMode.Fill -> ImageView.ScaleType.CENTER_CROP
                            MediaRenderScaleMode.Fit -> ImageView.ScaleType.FIT_CENTER
                        }

                        imageView.load(resolveImageModel(media.localPath))
                    }
                )
            }

            is MediaAsset.Video -> {
                val playerKey = remember(media.localPath, replayToken) {
                    "${media.localPath}#$replayToken"
                }

                val uri = remember(media.localPath) {
                    resolveUri(media.localPath)
                }

                val exoPlayer = remember(playerKey) {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(uri))
                        repeatMode = Player.REPEAT_MODE_OFF
                        playWhenReady = false
                        prepare()
                    }
                }

                DisposableEffect(exoPlayer) {
                    latestCallback.value(
                        VideoPlaybackUiState(
                            isLoading = true,
                            canReplay = false
                        )
                    )

                    val listener = object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            when (playbackState) {
                                Player.STATE_BUFFERING -> {
                                    latestCallback.value(
                                        VideoPlaybackUiState(
                                            isLoading = true,
                                            canReplay = false
                                        )
                                    )
                                }

                                Player.STATE_READY -> {
                                    if (exoPlayer.isPlaying) {
                                        latestCallback.value(
                                            VideoPlaybackUiState(
                                                isLoading = false,
                                                canReplay = false
                                            )
                                        )
                                    }
                                }

                                Player.STATE_ENDED -> {
                                    latestCallback.value(
                                        VideoPlaybackUiState(
                                            isLoading = false,
                                            canReplay = true
                                        )
                                    )
                                }

                                Player.STATE_IDLE -> {
                                    latestCallback.value(VideoPlaybackUiState())
                                }
                            }
                        }

                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            if (isPlaying) {
                                latestCallback.value(
                                    VideoPlaybackUiState(
                                        isLoading = false,
                                        canReplay = false
                                    )
                                )
                            }
                        }
                    }

                    exoPlayer.addListener(listener)

                    onDispose {
                        exoPlayer.removeListener(listener)
                        exoPlayer.release()
                    }
                }

                LaunchedEffect(exoPlayer, isActive) {
                    if (isActive) {
                        if (exoPlayer.playbackState != Player.STATE_ENDED) {
                            exoPlayer.play()
                        }
                    } else {
                        exoPlayer.pause()
                    }
                }

                AndroidView(
                    modifier = modifier,
                    factory = {
                        PlayerView(context).apply {
                            useController = false
                            resizeMode = when (scaleMode) {
                                MediaRenderScaleMode.Fill -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                MediaRenderScaleMode.Fit -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                            }
                            this.player = exoPlayer
                        }
                    },
                    update = { playerView ->
                        playerView.player = exoPlayer
                        playerView.resizeMode = when (scaleMode) {
                            MediaRenderScaleMode.Fill -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            MediaRenderScaleMode.Fit -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                        }

                        if (isActive) {
                            if (exoPlayer.playbackState != Player.STATE_ENDED && !exoPlayer.isPlaying) {
                                exoPlayer.play()
                            }
                        } else {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            }
                        }
                    }
                )
            }
        }
    }

    private fun resolveImageModel(path: String): Any {
        return when {
            path.startsWith("content://") -> path.toUri()
            path.startsWith("file://") -> path.toUri()
            path.startsWith("http://") -> path
            path.startsWith("https://") -> path
            else -> Uri.fromFile(File(path))
        }
    }

    private fun resolveUri(path: String): Uri {
        return when {
            path.startsWith("content://") -> path.toUri()
            path.startsWith("file://") -> path.toUri()
            path.startsWith("http://") -> path.toUri()
            path.startsWith("https://") -> path.toUri()
            else -> Uri.fromFile(File(path))
        }
    }
}