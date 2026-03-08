package com.interview.rickandmortyturkcell.ui.photodetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.interview.rickandmortyturkcell.R
import com.interview.rickandmortyturkcell.ui.common.ZoomablePhoto
import com.interview.rickandmortyturkcell.ui.photodetail.model.PhotoDetailUiState
import com.interview.rickandmortyturkcell.ui.photodetail.model.UiMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    uiState: PhotoDetailUiState,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onMessageConsumed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val backContentDescription = stringResource(R.string.back)
    val downloadContentDescription = stringResource(R.string.download)

    val resolvedDownloadMessage = when (val uiMessage = uiState.downloadMessage) {
        null -> null
        is UiMessage.Resource -> stringResource(uiMessage.resId)
        is UiMessage.Dynamic -> uiMessage.value
    }

    LaunchedEffect(resolvedDownloadMessage) {
        resolvedDownloadMessage?.let {
            snackbarHostState.showSnackbar(it)
            onMessageConsumed()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        containerColor = Color.Black,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.25f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Text(text = uiState.photoName)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = backContentDescription
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onDownloadClick, enabled = !uiState.isDownloading
                    ) {
                        if (uiState.isDownloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = downloadContentDescription
                            )
                        }
                    }
                })
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .background(Color.Black), contentAlignment = Alignment.Center
        ) {
            ZoomablePhoto(
                imageUrl = uiState.photoUrl, contentDescription = uiState.photoName
            )
        }
    }
}
