import com.interview.domain.model.GalleryPhoto
import com.interview.domain.repository.GalleryRepository
import com.interview.domain.usecase.SaveGalleryPhotosUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveGalleryPhotosUseCaseTest {

    private val repository: GalleryRepository = mockk()
    private val useCase = SaveGalleryPhotosUseCase(repository)

    @Test
    fun `invoke should call repository with given gallery photos`() = runTest {
        val photos = listOf(
            GalleryPhoto(
                uri = "content://photo/1",
                displayName = "photo_1",
                dateTakenMillis = 1000L,
                addedAtMillis = 1100L
            ), GalleryPhoto(
                uri = "content://photo/2",
                displayName = "photo_2",
                dateTakenMillis = 2000L,
                addedAtMillis = 2100L
            )
        )

        coEvery { repository.saveGalleryPhotos(photos) } just runs

        useCase(photos)

        coVerify(exactly = 1) {
            repository.saveGalleryPhotos(photos)
        }
    }

    @Test
    fun `invoke should call repository with empty list`() = runTest {
        val photos = emptyList<GalleryPhoto>()

        coEvery { repository.saveGalleryPhotos(photos) } just runs

        useCase(photos)

        coVerify(exactly = 1) {
            repository.saveGalleryPhotos(photos)
        }
    }
}