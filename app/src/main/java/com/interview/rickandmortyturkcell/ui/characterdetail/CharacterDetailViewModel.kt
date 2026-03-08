package com.interview.rickandmortyturkcell.ui.characterdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interview.domain.usecase.ObserveCharacterDetailUseCase
import com.interview.rickandmortyturkcell.analytics.AppAnalytics
import com.interview.rickandmortyturkcell.navigation.AppDestinations
import com.interview.rickandmortyturkcell.ui.characterdetail.model.CharacterDetailUiState
import com.interview.rickandmortyturkcell.ui.characterdetail.model.toDetailModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeCharacterDetailUseCase: ObserveCharacterDetailUseCase,
    private val appAnalytics: AppAnalytics
) : ViewModel() {

    private val characterId: Int = checkNotNull(
        savedStateHandle[AppDestinations.CHARACTER_ID_ARG]
    )

    private var hasLoggedDetailOpened = false

    val uiState = observeCharacterDetailUseCase(characterId).map { character ->
            if (character != null && !hasLoggedDetailOpened) {
                appAnalytics.logCharacterDetailOpened(
                    characterId = character.id, characterName = character.name
                )
                hasLoggedDetailOpened = true
            }

            CharacterDetailUiState(
                isLoading = character == null, character = character?.toDetailModel()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CharacterDetailUiState()
        )
}
