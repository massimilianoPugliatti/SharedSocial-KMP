package com.example.sharedsocial_kmp.platform

import android.content.Context
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.sharedsocial_kmp.core.platform.MediaPreviewRenderer
import com.example.sharedsocial_kmp.features.camera.domain.model.MediaAsset
import java.io.File
import androidx.core.net.toUri

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
                        imageView.setImageURI(resolveUri(media.localPath))
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
                        val uri = resolveUri(media.localPath)
                        videoView.setVideoURI(uri)
                        videoView.seekTo(1)
                    }
                )
            }
        }
    }

    private fun resolveUri(path: String): Uri {
        return when {
            path.startsWith("content://") -> path.toUri()
            path.startsWith("file://") -> path.toUri()
            else -> Uri.fromFile(File(path))
        }
    }
}