package com.interview.rickandmortyturkcell.ui.characterlist

import android.net.Uri
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interview.rickandmortyturkcell.R
import com.interview.rickandmortyturkcell.ui.characterlist.model.CharacterGridUiModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CharacterListRoute(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onCharacterClick: (Int) -> Unit,
    onGalleryItemClick: (Uri, String) -> Unit,
    viewModel: CharacterListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val galleryItems by viewModel.galleryItems.collectAsStateWithLifecycle()
    val galleryFallbackName = stringResource(R.string.gallery_photo)

    CharacterListScreen(
        uiState = uiState,
        galleryItems = galleryItems,
        characterItems = uiState.characterItems,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        onPhotosPicked = viewModel::onPhotosPicked,
        onSortOrderChanged = viewModel::onSortChangedAndRefresh,
        onRefresh = viewModel::refreshCharacters,
        onLoadNextPage = viewModel::loadNextPage,
        onRetryAppend = viewModel::retryAppend,
        onGridItemClick = { item ->
            when (item) {
                is CharacterGridUiModel.CharacterItem -> {
                    onCharacterClick(item.id)
                }

                is CharacterGridUiModel.GalleryItem -> {
                    onGalleryItemClick(
                        item.uri.toUri(), item.displayName ?: galleryFallbackName
                    )
                }
            }
        },
        onErrorConsumed = viewModel::consumeErrorMessage,
        onAppendErrorConsumed = viewModel::consumeAppendErrorMessage
    )
}
