package com.example.sharedsocial_kmp.platform

import android.content.Context
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import coil.load
import com.example.sharedsocial_kmp.core.platform.MediaPreviewRenderer
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import java.io.File

class AndroidMediaPreviewRenderer(
    private val context: Context,
) : MediaPreviewRenderer {

    @Composable
    override fun Render(
        media: MediaAsset,
        modifier: Modifier,
    ) {
        when (media) {
            is MediaAsset.Photo -> {
                AndroidView(
                    modifier = modifier,
                    factory = {
                        ImageView(context).apply {
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                MATCH_PARENT,
                                MATCH_PARENT
                            )
                            scaleType = ImageView.ScaleType.FIT_CENTER
                        }
                    },
                    update = { imageView ->
                        imageView.load(resolveImageModel(media.localPath))
                    }
                )
            }

            is MediaAsset.Video -> {
                AndroidView(
                    modifier = modifier,
                    factory = {
                        VideoView(context).apply {
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                MATCH_PARENT,
                                MATCH_PARENT
                            )
                            setOnPreparedListener { mp ->
                                mp.isLooping = true
                                start()
                            }
                        }
                    },
                    update = { videoView ->
                        val uri = resolveVideoUri(media.localPath)
                        videoView.setVideoURI(uri)
                        videoView.seekTo(1)
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

    private fun resolveVideoUri(path: String): Uri {
        return when {
            path.startsWith("content://") -> path.toUri()
            path.startsWith("file://") -> path.toUri()
            path.startsWith("http://") -> path.toUri()
            path.startsWith("https://") -> path.toUri()
            else -> Uri.fromFile(File(path))
        }
    }
}