package com.interview.rickandmortyturkcell.ui.photodetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PhotoDetailRoute(
    onBackClick: () -> Unit,
    viewModel: PhotoDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PhotoDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onDownloadClick = viewModel::downloadPhoto,
        onMessageConsumed = viewModel::consumeMessage
    )
}
