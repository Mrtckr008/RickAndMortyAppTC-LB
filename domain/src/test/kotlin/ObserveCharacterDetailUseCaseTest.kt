import com.interview.domain.model.Character
import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterPlace
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus
import com.interview.domain.repository.CharacterRepository
import com.interview.domain.usecase.ObserveCharacterDetailUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertSame
import org.junit.Test

class ObserveCharacterDetailUseCaseTest {

    private val repository: CharacterRepository = mockk()
    private val useCase = ObserveCharacterDetailUseCase(repository)

    @Test
    fun `invoke should return repository flow with character`() {
        val character = Character(
            id = 1,
            name = "Rick Sanchez",
            status = CharacterStatus.ALIVE,
            species = CharacterSpecies.HUMAN,
            type = "",
            gender = CharacterGender.MALE,
            origin = CharacterPlace(
                name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"
            ),
            location = CharacterPlace(
                name = "Citadel of Ricks", url = "https://rickandmortyapi.com/api/location/3"
            ),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            episodes = emptyList(),
            url = "https://rickandmortyapi.com/api/character/1",
            created = "2017-11-04T18:48:46.250Z"
        )

        val expectedFlow: Flow<Character?> = flowOf(character)

        every { repository.observeCharacterById(1) } returns expectedFlow

        val result = useCase(1)

        assertSame(expectedFlow, result)
        verify(exactly = 1) {
            repository.observeCharacterById(1)
        }
    }

    @Test
    fun `invoke should return repository flow with null`() {
        val expectedFlow: Flow<Character?> = flowOf(null)

        every { repository.observeCharacterById(99) } returns expectedFlow

        val result = useCase(99)

        assertSame(expectedFlow, result)
        verify(exactly = 1) {
            repository.observeCharacterById(99)
        }
    }
}