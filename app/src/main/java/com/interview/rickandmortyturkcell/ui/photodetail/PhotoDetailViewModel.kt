package com.interview.rickandmortyturkcell.ui.photodetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.interview.rickandmortyturkcell.R
import com.interview.rickandmortyturkcell.analytics.AppAnalytics
import com.interview.rickandmortyturkcell.download.PhotoDownloader
import com.interview.rickandmortyturkcell.ui.photodetail.model.PhotoDetailUiState
import com.interview.rickandmortyturkcell.ui.photodetail.model.UiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

const val PHOTO_URL = "photoUrl"
const val PHOTO_NAME = "photoName"

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photoDownloader: PhotoDownloader,
    private val appAnalytics: AppAnalytics
) : ViewModel() {

    private val rawPhotoUrl: String = checkNotNull(savedStateHandle[PHOTO_URL])
    private val rawPhotoName: String = checkNotNull(savedStateHandle[PHOTO_NAME])

    private val _uiState = MutableStateFlow(
        PhotoDetailUiState(
            photoUrl = rawPhotoUrl.urlDecode(), photoName = rawPhotoName.urlDecode()
        )
    )
    val uiState: StateFlow<PhotoDetailUiState> = _uiState.asStateFlow()

    fun downloadPhoto() {
        val currentState = _uiState.value
        if (currentState.isDownloading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDownloading = true, downloadMessage = null
                )
            }

            val result = photoDownloader.downloadToGallery(
                photoSource = currentState.photoUrl, photoName = currentState.photoName
            )

            if (result.isSuccess) {
                appAnalytics.logPhotoDownloaded(currentState.photoName)
            } else {
                result.exceptionOrNull()?.let { throwable ->
                    FirebaseCrashlytics.getInstance().recordException(throwable)
                }
            }

            _uiState.update {
                it.copy(
                    isDownloading = false, downloadMessage = if (result.isSuccess) {
                        UiMessage.Resource(R.string.photo_saved_to_gallery)
                    } else {
                        UiMessage.Resource(R.string.download_failed)
                    }
                )
            }
        }
    }

    fun consumeMessage() {
        _uiState.update { it.copy(downloadMessage = null) }
    }
}

private fun String.urlDecode(): String {
    return URLDecoder.decode(this, StandardCharsets.UTF_8.toString())
}
