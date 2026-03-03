package com.example.sharedsocial_kmp.features.feed.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sharedsocial_kmp.features.feed.domain.model.Post


@Composable
fun PostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier,
    showAuthor: Boolean = true
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (showAuthor) {
                PostAuthorHeader(username = post.author.username)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            PostInteractionRow(
                liked = post.liked,
                likesCount = post.likesCount,
                onLikeClick = onLikeClick,
                onCommentClick = onCommentClick
            )
        }
    }
}

@Composable
private fun PostAuthorHeader(username: String) {
    Text(
        text = username,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun PostInteractionRow(
    liked: Boolean,
    likesCount: Int,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        FilledTonalIconToggleButton(
            checked = liked,
            onCheckedChange = { onLikeClick() },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Like",
                tint = if (liked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "$likesCount",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onCommentClick) {
            Text("Commenti")
        }
    }
}