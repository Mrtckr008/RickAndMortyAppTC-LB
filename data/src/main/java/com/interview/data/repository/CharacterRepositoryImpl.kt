package com.interview.data.repository

import androidx.room.withTransaction
import com.interview.data.di.IoDispatcher
import com.interview.data.local.db.AppDatabase
import com.interview.data.local.entity.CharacterEntity
import com.interview.data.mapper.toDomain
import com.interview.data.mapper.toEntity
import com.interview.data.remote.api.CharacterApiService
import com.interview.data.utils.retryApiCall
import com.interview.domain.model.Character
import com.interview.domain.model.CharacterListSortOrder
import com.interview.domain.model.CharacterPageResult
import com.interview.domain.repository.CharacterRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val apiService: CharacterApiService,
    private val database: AppDatabase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CharacterRepository {

    private val characterDao = database.characterDao()

    override suspend fun fetchCharactersPage(
        page: Int?, clearBeforeInsert: Boolean, sortOrder: CharacterListSortOrder
    ): Result<CharacterPageResult> = withContext(ioDispatcher) {
        runCatching {
            val targetPage = resolveTargetPage(
                requestedPage = page, sortOrder = sortOrder
            )

            val response = fetchCharactersPageFromApi(targetPage)
            val entities = response.results.orEmpty().map { it.toEntity(targetPage) }

            database.withTransaction {
                if (clearBeforeInsert) {
                    characterDao.clearAll()
                }
                characterDao.insertAll(entities)
            }

            CharacterPageResult(
                items = mapPageItems(
                    entities = entities, sortOrder = sortOrder
                ), nextPage = calculateNextPage(
                    sortOrder = sortOrder,
                    targetPage = targetPage,
                    hasNextPage = response.info?.next != null
                ), endReached = calculateEndReached(
                    sortOrder = sortOrder,
                    targetPage = targetPage,
                    resultsEmpty = entities.isEmpty(),
                    hasNextPage = response.info?.next != null
                )
            )
        }
    }

    override fun observeCharacterById(characterId: Int): Flow<Character?> {
        return characterDao.observeCharacterById(characterId).map { entity -> entity?.toDomain() }
    }

    private suspend fun resolveTargetPage(
        requestedPage: Int?, sortOrder: CharacterListSortOrder
    ): Int {
        return when {
            requestedPage != null -> requestedPage
            sortOrder == CharacterListSortOrder.OLDEST_FIRST -> FIRST_PAGE
            else -> fetchLastPage()
        }
    }

    private suspend fun fetchLastPage(): Int {
        val bootstrapResponse = retryApiCall(
            maxRetries = API_MAX_RETRIES, initialDelayMillis = API_INITIAL_DELAY_MS
        ) {
            apiService.getCharacters(FIRST_PAGE)
        }
        return bootstrapResponse.info?.pages ?: FIRST_PAGE
    }

    private suspend fun fetchCharactersPageFromApi(page: Int) = retryApiCall(
        maxRetries = API_MAX_RETRIES, initialDelayMillis = API_INITIAL_DELAY_MS
    ) {
        apiService.getCharacters(page)
    }

    private fun mapPageItems(
        entities: List<CharacterEntity>, sortOrder: CharacterListSortOrder
    ): List<Character> {
        return when (sortOrder) {
            CharacterListSortOrder.NEWEST_FIRST -> {
                entities.sortedByDescending { it.createdAtMillis }
            }

            CharacterListSortOrder.OLDEST_FIRST -> {
                entities.sortedBy { it.createdAtMillis }
            }
        }.map { it.toDomain() }
    }

    private fun calculateNextPage(
        sortOrder: CharacterListSortOrder, targetPage: Int, hasNextPage: Boolean
    ): Int? {
        return when (sortOrder) {
            CharacterListSortOrder.OLDEST_FIRST -> {
                if (hasNextPage) targetPage + 1 else null
            }

            CharacterListSortOrder.NEWEST_FIRST -> {
                if (targetPage > FIRST_PAGE) targetPage - 1 else null
            }
        }
    }

    private fun calculateEndReached(
        sortOrder: CharacterListSortOrder,
        targetPage: Int,
        resultsEmpty: Boolean,
        hasNextPage: Boolean
    ): Boolean {
        return when (sortOrder) {
            CharacterListSortOrder.OLDEST_FIRST -> {
                resultsEmpty || !hasNextPage
            }

            CharacterListSortOrder.NEWEST_FIRST -> {
                resultsEmpty || targetPage <= FIRST_PAGE
            }
        }
    }

    private companion object {
        const val FIRST_PAGE = 1
        const val API_MAX_RETRIES = 3
        const val API_INITIAL_DELAY_MS = 500L
    }
}
