package com.interview.rickandmortyturkcell.characterdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.interview.domain.model.Character
import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterPlace
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus
import com.interview.domain.usecase.ObserveCharacterDetailUseCase
import com.interview.rickandmortyturkcell.MainDispatcherRule
import com.interview.rickandmortyturkcell.analytics.AppAnalytics
import com.interview.rickandmortyturkcell.ui.characterdetail.CharacterDetailViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CharacterDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeCharacterDetailUseCase: ObserveCharacterDetailUseCase = mockk()
    private val appAnalytics: AppAnalytics = mockk(relaxed = true)

    @Test
    fun `uiState should emit mapped character and log analytics when use case returns character`() =
        runTest {
            val savedStateHandle = SavedStateHandle(
                mapOf("characterId" to 18)
            )

            val character = Character(
                id = 18,
                name = "Antenna Morty",
                status = CharacterStatus.ALIVE,
                species = CharacterSpecies.HUMAN,
                type = "Human with antennae",
                gender = CharacterGender.MALE,
                origin = CharacterPlace(
                    name = "Earth (Replacement Dimension)",
                    url = "https://rickandmortyapi.com/api/location/20"
                ),
                location = CharacterPlace(
                    name = "Citadel of Ricks", url = "https://rickandmortyapi.com/api/location/3"
                ),
                image = "https://rickandmortyapi.com/api/character/avatar/18.jpeg",
                episodes = listOf(
                    "https://rickandmortyapi.com/api/episode/10"
                ),
                url = "https://rickandmortyapi.com/api/character/18",
                created = "2017-11-04T22:25:29.008Z"
            )

            every { observeCharacterDetailUseCase(18) } returns flowOf(character)

            val viewModel = CharacterDetailViewModel(
                savedStateHandle = savedStateHandle,
                observeCharacterDetailUseCase = observeCharacterDetailUseCase,
                appAnalytics = appAnalytics
            )

            viewModel.uiState.test {
                val initial = awaitItem()
                assertEquals(true, initial.isLoading)
                assertEquals(null, initial.character)

                val loaded = awaitItem()
                assertEquals(false, loaded.isLoading)
                requireNotNull(loaded.character)

                assertEquals(18, loaded.character!!.id)
                assertEquals("Antenna Morty", loaded.character!!.name)
                assertEquals(CharacterStatus.ALIVE, loaded.character!!.status)
                assertEquals(CharacterSpecies.HUMAN, loaded.character!!.species)
                assertEquals(CharacterGender.MALE, loaded.character!!.gender)
                assertEquals("Earth (Replacement Dimension)", loaded.character!!.originName)
                assertEquals("Citadel of Ricks", loaded.character!!.locationName)
                assertEquals(
                    "https://rickandmortyapi.com/api/character/avatar/18.jpeg",
                    loaded.character!!.imageUrl
                )

                cancelAndIgnoreRemainingEvents()
            }

            verify(exactly = 1) {
                appAnalytics.logCharacterDetailOpened(18, "Antenna Morty")
            }
        }

    @Test
    fun `viewModel should use characterId from savedStateHandle`() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf("characterId" to 42)
        )

        every { observeCharacterDetailUseCase(42) } returns flowOf(null)

        CharacterDetailViewModel(
            savedStateHandle = savedStateHandle,
            observeCharacterDetailUseCase = observeCharacterDetailUseCase,
            appAnalytics = appAnalytics
        )

        verify(exactly = 1) {
            observeCharacterDetailUseCase(42)
        }
    }
}