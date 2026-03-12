package com.example.sharedsocial_kmp.features.createpost.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostContent(
    state: CreatePostState,
    preview: @Composable (Modifier) -> Unit,
    onEvent: (CreatePostEvent) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(CreatePostEvent.OnMessageConsumed)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Nuovo post") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(CreatePostEvent.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(CreatePostEvent.OnSubmitClick) },
                        enabled = !state.isSubmitting
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Invia")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
            ) {
                preview(Modifier.fillMaxSize())
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.surface),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.caption,
                    onValueChange = { onEvent(CreatePostEvent.OnCaptionChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Scrivi una descrizione...") },
                    minLines = 3,
                    maxLines = 5,
                    enabled = !state.isSubmitting,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
            }
        }
    }
}