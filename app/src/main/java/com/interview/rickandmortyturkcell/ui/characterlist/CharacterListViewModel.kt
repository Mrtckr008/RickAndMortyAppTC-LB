package com.interview.rickandmortyturkcell.ui.characterlist

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interview.domain.model.Character
import com.interview.domain.model.CharacterListSortOrder
import com.interview.domain.model.GalleryPhoto
import com.interview.domain.usecase.GetPagedCharactersUseCase
import com.interview.domain.usecase.ObserveGalleryPhotosUseCase
import com.interview.domain.usecase.SaveGalleryPhotosUseCase
import com.interview.rickandmortyturkcell.ui.characterlist.model.CharacterGridUiModel
import com.interview.rickandmortyturkcell.ui.characterlist.model.CharacterListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val characterRepository: GetPagedCharactersUseCase,
    private val observeGalleryPhotosUseCase: ObserveGalleryPhotosUseCase,
    private val saveGalleryPhotosUseCase: SaveGalleryPhotosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterListUiState())
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var isRequestRunning = false
    private var cachedCharacters: List<Character> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val galleryItems: StateFlow<List<CharacterGridUiModel.GalleryItem>> =
        _uiState.map { it.characterListSortOrder }.distinctUntilChanged()
            .flatMapLatest { sortOrder ->
                observeGalleryPhotosUseCase(
                    isNewestFirst = sortOrder == CharacterListSortOrder.NEWEST_FIRST
                )
            }.map { photos ->
                photos.map { photo ->
                    CharacterGridUiModel.GalleryItem(
                        uri = photo.uri,
                        displayName = photo.displayName,
                        dateTakenMillis = photo.dateTakenMillis
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        refreshCharacters()
    }

    fun refreshCharacters() {
        if (isRequestRunning) return

        viewModelScope.launch {
            isRequestRunning = true
            currentPage = 1

            _uiState.update {
                it.copy(
                    isRefreshing = true,
                    isInitialLoading = it.characterItems.isEmpty(),
                    endReached = false,
                    errorMessage = null,
                    appendErrorMessage = null
                )
            }

            val result = characterRepository(
                page = null,
                clearBeforeInsert = true,
                sortOrder = _uiState.value.characterListSortOrder
            )

            result.onSuccess { pageResult ->
                cachedCharacters = pageResult.items
                currentPage = pageResult.nextPage ?: 1

                _uiState.update {
                    it.copy(
                        characterItems = cachedCharacters.map { character -> character.toGridItem() },
                        isRefreshing = false,
                        isInitialLoading = false,
                        endReached = pageResult.endReached,
                        errorMessage = null,
                        appendErrorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        isInitialLoading = false,
                        errorMessage = throwable.message
                    )
                }
            }

            isRequestRunning = false
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (isRequestRunning || state.endReached || state.isRefreshing || state.isInitialLoading) return

        viewModelScope.launch {
            isRequestRunning = true

            _uiState.update {
                it.copy(
                    isAppending = true, appendErrorMessage = null
                )
            }

            val result = characterRepository(
                page = currentPage,
                clearBeforeInsert = false,
                sortOrder = _uiState.value.characterListSortOrder
            )

            result.onSuccess { pageResult ->
                cachedCharacters = (cachedCharacters + pageResult.items).distinctBy { it.id }

                currentPage = pageResult.nextPage ?: currentPage

                _uiState.update {
                    it.copy(
                        characterItems = cachedCharacters.map { character -> character.toGridItem() },
                        isAppending = false,
                        endReached = pageResult.endReached,
                        appendErrorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isAppending = false, appendErrorMessage = throwable.message
                    )
                }
            }

            isRequestRunning = false
        }
    }

    fun retryAppend() {
        loadNextPage()
    }

    fun onSortOrderChanged(sortOrder: CharacterListSortOrder) {
        if (_uiState.value.characterListSortOrder == sortOrder) return

        currentPage = 1
        isRequestRunning = false
        cachedCharacters = emptyList()

        _uiState.update {
            it.copy(
                characterListSortOrder = sortOrder,
                characterItems = emptyList(),
                isInitialLoading = true,
                isRefreshing = false,
                isAppending = false,
                endReached = false,
                errorMessage = null,
                appendErrorMessage = null,
                listResetKey = it.listResetKey + 1
            )
        }

        refreshCharacters()
    }

    fun onSortChangedAndRefresh(sortOrder: CharacterListSortOrder) {
        if (_uiState.value.characterListSortOrder == sortOrder) return

        currentPage = 1
        isRequestRunning = false
        cachedCharacters = emptyList()

        _uiState.update {
            it.copy(
                characterListSortOrder = sortOrder,
                characterItems = emptyList(),
                isInitialLoading = true,
                isRefreshing = false,
                isAppending = false,
                endReached = false,
                errorMessage = null,
                appendErrorMessage = null,
                listResetKey = it.listResetKey + 1
            )
        }

        refreshCharacters()
    }

    fun onPhotosPicked(uris: List<Uri>) {
        if (uris.isEmpty()) return

        viewModelScope.launch {
            runCatching {
                val now = System.currentTimeMillis()
                val uniqueUris = uris.distinctBy { it.toString() }

                val photos = uniqueUris.mapIndexed { index, uri ->
                    GalleryPhoto(
                        uri = uri.toString(),
                        displayName = null,
                        dateTakenMillis = now - index,
                        addedAtMillis = now
                    )
                }

                saveGalleryPhotosUseCase(photos)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        errorMessage = throwable.message
                    )
                }
            }
        }
    }

    fun consumeErrorMessage() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun consumeAppendErrorMessage() {
        _uiState.update {
            it.copy(appendErrorMessage = null)
        }
    }

    private fun Character.toGridItem(): CharacterGridUiModel.CharacterItem {
        return CharacterGridUiModel.CharacterItem(
            id = id,
            name = name,
            imageUrl = image,
            status = status,
            species = species,
            gender = gender
        )
    }
}