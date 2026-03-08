import com.interview.domain.model.Character
import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterListSortOrder
import com.interview.domain.model.CharacterPageResult
import com.interview.domain.model.CharacterPlace
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus
import com.interview.domain.repository.CharacterRepository
import com.interview.domain.usecase.GetPagedCharactersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class GetPagedCharactersUseCaseTest {

    private val repository: CharacterRepository = mockk()
    private val useCase = GetPagedCharactersUseCase(repository)

    @Test
    fun `invoke should call repository with given params and return success result`() = runTest {
        val expected = Result.success(
            CharacterPageResult(
                items = listOf(
                    Character(
                        id = 1,
                        name = "Rick Sanchez",
                        status = CharacterStatus.ALIVE,
                        species = CharacterSpecies.HUMAN,
                        type = "",
                        gender = CharacterGender.MALE,
                        origin = CharacterPlace(
                            name = "Earth (C-137)", url = ""
                        ),
                        location = CharacterPlace(
                            name = "Citadel of Ricks", url = ""
                        ),
                        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                        episodes = emptyList(),
                        url = "",
                        created = "2017-11-04T18:48:46.250Z"
                    )
                ), nextPage = 2, endReached = false
            )
        )

        coEvery {
            repository.fetchCharactersPage(
                page = 1, clearBeforeInsert = true, sortOrder = CharacterListSortOrder.NEWEST_FIRST
            )
        } returns expected

        val result = useCase(
            page = 1, clearBeforeInsert = true, sortOrder = CharacterListSortOrder.NEWEST_FIRST
        )

        assertTrue(result.isSuccess)
        assertEquals(expected.getOrNull(), result.getOrNull())

        coVerify(exactly = 1) {
            repository.fetchCharactersPage(
                page = 1, clearBeforeInsert = true, sortOrder = CharacterListSortOrder.NEWEST_FIRST
            )
        }
    }

    @Test
    fun `invoke should call repository with null page and return failure result`() = runTest {
        val throwable = IllegalStateException("Failed to fetch page")
        val expected = Result.failure<CharacterPageResult>(throwable)

        coEvery {
            repository.fetchCharactersPage(
                page = null,
                clearBeforeInsert = false,
                sortOrder = CharacterListSortOrder.OLDEST_FIRST
            )
        } returns expected

        val result = useCase(
            page = null, clearBeforeInsert = false, sortOrder = CharacterListSortOrder.OLDEST_FIRST
        )

        assertTrue(result.isFailure)
        assertEquals(
            expected.exceptionOrNull()?.message, result.exceptionOrNull()?.message
        )

        coVerify(exactly = 1) {
            repository.fetchCharactersPage(
                page = null,
                clearBeforeInsert = false,
                sortOrder = CharacterListSortOrder.OLDEST_FIRST
            )
        }
    }
}