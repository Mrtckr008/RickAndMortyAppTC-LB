package com.interview.domain.usecase

import com.interview.domain.model.Character
import com.interview.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCharacterDetailUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(characterId: Int): Flow<Character?> {
        return repository.observeCharacterById(characterId)
    }
}