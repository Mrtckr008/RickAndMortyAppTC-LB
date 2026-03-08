package com.interview.rickandmortyturkcell.ui.characterlist.model

import com.interview.domain.model.CharacterListSortOrder

data class CharacterListUiState(
    val characterItems: List<CharacterGridUiModel.CharacterItem> = emptyList(),
    val characterListSortOrder: CharacterListSortOrder = CharacterListSortOrder.OLDEST_FIRST,
    val isInitialLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isAppending: Boolean = false,
    val endReached: Boolean = false,
    val errorMessage: String? = null,
    val appendErrorMessage: String? = null,
    val listResetKey: Int = 0
)