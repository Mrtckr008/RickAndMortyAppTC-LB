package com.interview.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.interview.data.local.db.AppDatabase
import com.interview.data.remote.api.CharacterApiService
import com.interview.data.remote.dto.CharacterDto
import com.interview.data.remote.dto.CharacterListResponseDto
import com.interview.data.remote.dto.InfoDto
import com.interview.data.remote.dto.PlaceDto
import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterListSortOrder
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterRepositoryImplTest {

    private val apiService: CharacterApiService = mockk()
    private val testDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()

    private lateinit var database: AppDatabase
    private lateinit var repository: CharacterRepositoryImpl

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = CharacterRepositoryImpl(
            apiService = apiService, database = database, ioDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun fetchCharactersPage_oldestFirst_withNullPage_fetchesPage1_andClearsBeforeInsert() =
        runTest {
            val response = characterListResponse(
                pages = 42, next = "next_page_url", results = listOf(
                    characterDto(
                        id = 1, name = "Rick Sanchez", created = "2017-11-04T18:48:46.250Z"
                    ), characterDto(
                        id = 2, name = "Morty Smith", created = "2017-11-05T18:48:46.250Z"
                    )
                )
            )

            coEvery { apiService.getCharacters(1) } returns response

            val result = repository.fetchCharactersPage(
                page = null,
                clearBeforeInsert = true,
                sortOrder = CharacterListSortOrder.OLDEST_FIRST
            )

            assertTrue(result.isSuccess)
            val pageResult = result.getOrNull()
            requireNotNull(pageResult)

            assertEquals(2, pageResult.items.size)
            assertEquals("Rick Sanchez", pageResult.items[0].name)
            assertEquals("Morty Smith", pageResult.items[1].name)
            assertEquals(2, pageResult.nextPage)
            assertFalse(pageResult.endReached)

            coVerify(exactly = 1) { apiService.getCharacters(1) }

            val stored = database.characterDao().observeCharacterById(1).first()
            assertEquals("Rick Sanchez", stored?.name)
        }

    @Test
    fun fetchCharactersPage_newestFirst_withNullPage_bootstrapsAndFetchesLastPage() = runTest {
        val bootstrapResponse = characterListResponse(
            pages = 42,
            next = "page_2",
            results = listOf(characterDto(id = 1, name = "Rick Sanchez"))
        )

        val lastPageResponse = characterListResponse(
            pages = 42, next = null, results = listOf(
                characterDto(
                    id = 826, name = "Latest Character", created = "2025-01-01T10:00:00.000Z"
                ), characterDto(
                    id = 825, name = "Older Character", created = "2024-12-01T10:00:00.000Z"
                )
            )
        )

        coEvery { apiService.getCharacters(1) } returns bootstrapResponse
        coEvery { apiService.getCharacters(42) } returns lastPageResponse

        val result = repository.fetchCharactersPage(
            page = null, clearBeforeInsert = true, sortOrder = CharacterListSortOrder.NEWEST_FIRST
        )

        assertTrue(result.isSuccess)
        val pageResult = result.getOrNull()
        requireNotNull(pageResult)

        assertEquals(2, pageResult.items.size)
        assertEquals("Latest Character", pageResult.items[0].name)
        assertEquals("Older Character", pageResult.items[1].name)
        assertEquals(41, pageResult.nextPage)
        assertFalse(pageResult.endReached)

        coVerify(exactly = 1) { apiService.getCharacters(1) }
        coVerify(exactly = 1) { apiService.getCharacters(42) }
    }

    @Test
    fun observeCharacterById_should_map_entity_to_domain() = runTest {
        val response = characterListResponse(
            pages = 1, next = null, results = listOf(
                characterDto(
                    id = 1, name = "Rick Sanchez", created = "2017-11-04T18:48:46.250Z"
                )
            )
        )

        coEvery { apiService.getCharacters(1) } returns response

        repository.fetchCharactersPage(
            page = 1, clearBeforeInsert = true, sortOrder = CharacterListSortOrder.OLDEST_FIRST
        )

        val result = repository.observeCharacterById(1).first()
        requireNotNull(result)

        assertEquals(1, result.id)
        assertEquals("Rick Sanchez", result.name)
        assertEquals(CharacterStatus.ALIVE, result.status)
        assertEquals(CharacterSpecies.HUMAN, result.species)
        assertEquals(CharacterGender.MALE, result.gender)
    }

    private fun characterListResponse(
        pages: Int, next: String?, results: List<CharacterDto>
    ): CharacterListResponseDto {
        return CharacterListResponseDto(
            info = InfoDto(
                count = 826, pages = pages, next = next, prev = null
            ), results = results
        )
    }

    private fun characterDto(
        id: Int, name: String, created: String = "2017-11-04T18:48:46.250Z"
    ): CharacterDto {
        return CharacterDto(
            id = id,
            name = name,
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = PlaceDto(
                name = "Earth (C-137)", url = ""
            ),
            location = PlaceDto(
                name = "Citadel of Ricks", url = ""
            ),
            image = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
            episode = emptyList(),
            url = "https://rickandmortyapi.com/api/character/$id",
            created = created
        )
    }
}