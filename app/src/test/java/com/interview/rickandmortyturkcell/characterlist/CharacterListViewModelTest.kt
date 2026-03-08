package com.interview.rickandmortyturkcell.characterlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.interview.domain.model.Character
import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterListSortOrder
import com.interview.domain.model.CharacterPageResult
import com.interview.domain.model.CharacterPlace
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus
import com.interview.domain.usecase.GetPagedCharactersUseCase
import com.interview.domain.usecase.ObserveGalleryPhotosUseCase
import com.interview.domain.usecase.SaveGalleryPhotosUseCase
import com.interview.rickandmortyturkcell.MainDispatcherRule
import com.interview.rickandmortyturkcell.ui.characterlist.CharacterListViewModel
import com.interview.rickandmortyturkcell.ui.characterlist.model.CharacterListUiState
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CharacterListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getPagedCharactersUseCase = mockk<GetPagedCharactersUseCase>()
    private val observeGalleryPhotosUseCase = mockk<ObserveGalleryPhotosUseCase>()
    private val saveGalleryPhotosUseCase = mockk<SaveGalleryPhotosUseCase>()

    private lateinit var viewModel: CharacterListViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    private fun createViewModel() {
        viewModel = CharacterListViewModel(
            characterRepository = getPagedCharactersUseCase,
            observeGalleryPhotosUseCase = observeGalleryPhotosUseCase,
            saveGalleryPhotosUseCase = saveGalleryPhotosUseCase
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `init refreshCharacters success updates uiState correctly`() = runTest {
        val initialSort = CharacterListUiState().characterListSortOrder

        val characters = listOf(
            character(id = 1, name = "Rick"), character(id = 2, name = "Morty")
        )

        coEvery {
            getPagedCharactersUseCase(
                page = null, clearBeforeInsert = true, sortOrder = initialSort
            )
        } returns Result.success(
            pageResult(
                items = characters, nextPage = 2, endReached = false
            )
        )

        every {
            observeGalleryPhotosUseCase(any())
        } returns flowOf(emptyList())

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(2, state.characterItems.size)
        assertEquals("Rick", state.characterItems[0].name)
        assertEquals("Morty", state.characterItems[1].name)
        assertFalse(state.isRefreshing)
        assertFalse(state.isInitialLoading)
        assertFalse(state.endReached)
        assertNull(state.errorMessage)

        coVerify(exactly = 1) {
            getPagedCharactersUseCase(
                page = null, clearBeforeInsert = true, sortOrder = initialSort
            )
        }
    }

    @Test
    fun `init refreshCharacters failure updates errorMessage`() = runTest {
        val initialSort = CharacterListUiState().characterListSortOrder

        coEvery {
            getPagedCharactersUseCase(
                page = null, clearBeforeInsert = true, sortOrder = initialSort
            )
        } returns Result.failure(Throwable("network error"))

        every {
            observeGalleryPhotosUseCase(any())
        } returns flowOf(emptyList())

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertTrue(state.characterItems.isEmpty())
        assertFalse(state.isRefreshing)
        assertFalse(state.isInitialLoading)
        assertEquals("network error", state.errorMessage)
    }

    @Test
    fun `loadNextPage success appends unique items`() = runTest {
        val initialSort = CharacterListUiState().characterListSortOrder

        val firstPage = listOf(
            character(id = 1, name = "Rick"), character(id = 2, name = "Morty")
        )

        val secondPage = listOf(
            character(id = 2, name = "Morty"), character(id = 3, name = "Summer")
        )

        coEvery {
            getPagedCharactersUseCase(
                page = null, clearBeforeInsert = true, sortOrder = initialSort
            )
        } returns Result.success(
            pageResult(
                items = firstPage, nextPage = 2, endReached = false
            )
        )

        coEvery {
            getPagedCharactersUseCase(
                page = 2, clearBeforeInsert = false, sortOrder = initialSort
            )
        } returns Result.success(
            pageResult(
                items = secondPage, nextPage = 3, endReached = false
            )
        )

        every {
            observeGalleryPhotosUseCase(any())
        } returns flowOf(emptyList())

        createViewModel()
        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(3, state.characterItems.size)
        assertEquals(listOf(1, 2, 3), state.characterItems.map { it.id })
        assertFalse(state.isAppending)
        assertNull(state.appendErrorMessage)
    }

    @Test
    fun `loadNextPage failure updates appendErrorMessage`() = runTest {
        val initialSort = CharacterListUiState().characterListSortOrder

        val firstPage = listOf(
            character(id = 1, name = "Rick"), character(id = 2, name = "Morty")
        )

        coEvery {
            getPagedCharactersUseCase(
                page = null, clearBeforeInsert = true, sortOrder = initialSort
            )
        } returns Result.success(
            pageResult(
                items = firstPage, nextPage = 2, endReached = false
            )
        )

        coEvery {
            getPagedCharactersUseCase(
                page = 2, clearBeforeInsert = false, sortOrder = initialSort
            )
        } returns Result.failure(Throwable("append failed"))

        every {
            observeGalleryPhotosUseCase(any())
        } returns flowOf(emptyList())

        createViewModel()
        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(2, state.characterItems.size)
        assertFalse(state.isAppending)
        assertEquals("append failed", state.appendErrorMessage)
    }

    @Test
    fun `retryAppend calls loadNextPage logic`() = runTest {
        val initialSort = CharacterListUiState().characterListSortOrder

        val firstPage = listOf(character(id = 1, name = "Rick"))
        val secondPage = listOf(character(id = 2, name = "Morty"))

        coEvery {
            getPagedCharactersUseCase(
                page = null, clearBeforeInsert = true, sortOrder = initialSort
            )
        } returns Result.success(
            pageResult(items = firstPage, nextPage = 2, endReached = false)
        )

        coEvery {
            getPagedCharactersUseCase(
                page = 2, clearBeforeInsert = false, sortOrder = initialSort
            )
        } returns Result.success(
            pageResult(items = secondPage, nextPage = 3, endReached = false)
        )

        every {
            observeGalleryPhotosUseCase(any())
        } returns flowOf(emptyList())

        createViewModel()
        advanceUntilIdle()

        viewModel.retryAppend()
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.characterItems.size)
    }

    @Test
    fun `onSortOrderChanged with different sort resets list and refreshes`() = runTest {
        val initialSort = CharacterListUiState().characterListSortOrder
        val newSort = enumValues<CharacterListSortOrder>().first { it != initialSort }

        coEvery {
            getPagedCharactersUseCase(
                page = null, clearBeforeInsert = true, sortOrder = initialSort
            )
        } returns Result.success(
            pageResult(
                items = listOf(character(id = 1, name = "Rick")), nextPage = 2, endReached = false
            )
        )

        coEvery {
            getPagedCharactersUseCase(
                page = null, clearBeforeInsert = true, sortOrder = newSort
            )
        } returns Result.success(
            pageResult(
                items = listOf(character(id = 99, name = "Birdperson")),
                nextPage = 2,
                endReached = false
            )
        )

        every {
            observeGalleryPhotosUseCase(any())
        } returns flowOf(emptyList())

        createViewModel()
        advanceUntilIdle()

        val oldResetKey = viewModel.uiState.value.listResetKey

        viewModel.onSortOrderChanged(newSort)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(newSort, state.characterListSortOrder)
        assertEquals(1, state.characterItems.size)
        assertEquals("Birdperson", state.characterItems.first().name)
        assertEquals(oldResetKey + 1, state.listResetKey)

        coVerify {
            getPagedCharactersUseCase(
                page = null, clearBeforeInsert = true, sortOrder = newSort
            )
        }
    }

    @Test
    fun `onSortOrderChanged with same sort does nothing`() = runTest {
        val initialSort = CharacterListUiState().characterListSortOrder

        coEvery {
            getPagedCharactersUseCase(
                page = null, clearBeforeInsert = true, sortOrder = initialSort
            )
        } returns Result.success(
            pageResult(
                items = listOf(character(id = 1, name = "Rick")), nextPage = 2, endReached = false
            )
        )

        every {
            observeGalleryPhotosUseCase(any())
        } returns flowOf(emptyList())

        createViewModel()
        advanceUntilIdle()

        clearMocks(getPagedCharactersUseCase, answers = false)

        viewModel.onSortOrderChanged(initialSort)
        advanceUntilIdle()

        coVerify(exactly = 0) { getPagedCharactersUseCase(any(), any(), any()) }
    }

    @Test
    fun `consumeErrorMessage clears error`() = runTest {
        val initialSort = CharacterListUiState().characterListSortOrder

        coEvery {
            getPagedCharactersUseCase(
                page = null, clearBeforeInsert = true, sortOrder = initialSort
            )
        } returns Result.failure(Throwable("boom"))

        every {
            observeGalleryPhotosUseCase(any())
        } returns flowOf(emptyList())

        createViewModel()
        advanceUntilIdle()

        assertEquals("boom", viewModel.uiState.value.errorMessage)

        viewModel.consumeErrorMessage()

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `consumeAppendErrorMessage clears append error`() = runTest {
        val initialSort = CharacterListUiState().characterListSortOrder

        coEvery {
            getPagedCharactersUseCase(
                page = null, clearBeforeInsert = true, sortOrder = initialSort
            )
        } returns Result.success(
            pageResult(
                items = listOf(character(id = 1, name = "Rick")), nextPage = 2, endReached = false
            )
        )

        coEvery {
            getPagedCharactersUseCase(
                page = 2, clearBeforeInsert = false, sortOrder = initialSort
            )
        } returns Result.failure(Throwable("append fail"))

        every {
            observeGalleryPhotosUseCase(any())
        } returns flowOf(emptyList())

        createViewModel()
        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        assertEquals("append fail", viewModel.uiState.value.appendErrorMessage)

        viewModel.consumeAppendErrorMessage()

        assertNull(viewModel.uiState.value.appendErrorMessage)
    }

    private fun character(
        id: Int,
        name: String,
        image: String = "https://example.com/$id.png",
        status: CharacterStatus = CharacterStatus.ALIVE,
        species: CharacterSpecies = CharacterSpecies.HUMAN,
        type: String = "",
        gender: CharacterGender = CharacterGender.MALE,
        origin: CharacterPlace = CharacterPlace(
            name = "Earth", url = "https://example.com/location/origin"
        ),
        location: CharacterPlace = CharacterPlace(
            name = "Earth", url = "https://example.com/location/current"
        ),
        episodes: List<String> = emptyList(),
        url: String = "https://example.com/character/$id",
        created: String = "2024-01-01T00:00:00.000Z"
    ): Character {
        return Character(
            id = id,
            name = name,
            status = status,
            species = species,
            type = type,
            gender = gender,
            origin = origin,
            location = location,
            image = image,
            episodes = episodes,
            url = url,
            created = created
        )
    }

    private fun pageResult(
        items: List<Character>, nextPage: Int?, endReached: Boolean
    ): CharacterPageResult {
        return CharacterPageResult(
            items = items, nextPage = nextPage, endReached = endReached
        )
    }

    private fun assertFalse(value: Boolean) {
        assertTrue(!value)
    }
}