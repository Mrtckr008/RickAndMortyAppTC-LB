package com.interview.domain.usecase

import com.interview.domain.model.CharacterListSortOrder
import com.interview.domain.model.CharacterPageResult
import com.interview.domain.repository.CharacterRepository
import javax.inject.Inject

class GetPagedCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(
        page: Int?, clearBeforeInsert: Boolean, sortOrder: CharacterListSortOrder
    ): Result<CharacterPageResult> {
        return repository.fetchCharactersPage(
            page = page, clearBeforeInsert = clearBeforeInsert, sortOrder = sortOrder
        )
    }
}