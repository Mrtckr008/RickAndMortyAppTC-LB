package com.interview.rickandmortyturkcell.ui.photodetail.model

data class PhotoDetailUiState(
    val photoUrl: String = "",
    val photoName: String = "",
    val isDownloading: Boolean = false,
    val downloadMessage: UiMessage? = null
)