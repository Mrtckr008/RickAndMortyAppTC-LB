package com.interview.domain.repository

import com.interview.domain.model.Character
import com.interview.domain.model.CharacterListSortOrder
import com.interview.domain.model.CharacterPageResult
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    suspend fun fetchCharactersPage(
        page: Int?,
        clearBeforeInsert: Boolean,
        sortOrder: CharacterListSortOrder
    ): Result<CharacterPageResult>

    fun observeCharacterById(characterId: Int): Flow<Character?>
}