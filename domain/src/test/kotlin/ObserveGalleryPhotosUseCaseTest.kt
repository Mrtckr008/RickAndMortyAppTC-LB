import app.cash.turbine.test
import com.interview.domain.model.GalleryPhoto
import com.interview.domain.repository.GalleryRepository
import com.interview.domain.usecase.ObserveGalleryPhotosUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveGalleryPhotosUseCaseTest {

    private val repository: GalleryRepository = mockk()
    private val useCase = ObserveGalleryPhotosUseCase(repository)

    @Test
    fun `invoke should emit newest first gallery photos`() = runTest {
        val photos = listOf(
            GalleryPhoto(
                uri = "content://photo/1",
                displayName = "photo_1",
                dateTakenMillis = 300L,
                addedAtMillis = 301L
            ), GalleryPhoto(
                uri = "content://photo/2",
                displayName = "photo_2",
                dateTakenMillis = 200L,
                addedAtMillis = 201L
            )
        )

        every { repository.observeGalleryPhotos(true) } returns flowOf(photos)

        useCase(true).test {
            assertEquals(photos, awaitItem())
            awaitComplete()
        }
    }
}