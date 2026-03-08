package com.interview.data.repository

import app.cash.turbine.test
import com.interview.data.local.dao.GalleryPhotoDao
import com.interview.data.local.entity.GalleryPhotoEntity
import com.interview.domain.model.GalleryPhoto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GalleryRepositoryImplTest {

    private val galleryPhotoDao: GalleryPhotoDao = mockk()
    private val testDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
    private val repository = GalleryRepositoryImpl(galleryPhotoDao, testDispatcher)

    @Test
    fun `observeGalleryPhotos should map entities to domain models`() = runTest {
        val entities = listOf(
            GalleryPhotoEntity(
                uri = "content://gallery/photo1",
                displayName = "photo_1",
                dateTakenMillis = 1000L,
                addedAtMillis = 1100L
            ),
            GalleryPhotoEntity(
                uri = "content://gallery/photo2",
                displayName = "photo_2",
                dateTakenMillis = 2000L,
                addedAtMillis = 2100L
            )
        )

        every { galleryPhotoDao.observeGalleryPhotos(true) } returns flowOf(entities)

        repository.observeGalleryPhotos(isNewestFirst = true).test {
            val result = awaitItem()

            assertEquals(2, result.size)

            assertEquals("content://gallery/photo1", result[0].uri)
            assertEquals("photo_1", result[0].displayName)
            assertEquals(1000L, result[0].dateTakenMillis)
            assertEquals(1100L, result[0].addedAtMillis)

            assertEquals("content://gallery/photo2", result[1].uri)
            assertEquals("photo_2", result[1].displayName)
            assertEquals(2000L, result[1].dateTakenMillis)
            assertEquals(2100L, result[1].addedAtMillis)

            awaitComplete()
        }
    }

    @Test
    fun `observeGalleryPhotos should emit empty list when dao returns empty list`() = runTest {
        every { galleryPhotoDao.observeGalleryPhotos(false) } returns flowOf(emptyList())

        repository.observeGalleryPhotos(isNewestFirst = false).test {
            val result = awaitItem()
            assertEquals(emptyList<GalleryPhoto>(), result)
            awaitComplete()
        }
    }

    @Test
    fun `saveGalleryPhotos should map domain models to entities and insert them`() = runTest {
        val photos = listOf(
            GalleryPhoto(
                uri = "content://gallery/photo1",
                displayName = "photo_1",
                dateTakenMillis = 1000L,
                addedAtMillis = 1100L
            ),
            GalleryPhoto(
                uri = "content://gallery/photo2",
                displayName = "photo_2",
                dateTakenMillis = 2000L,
                addedAtMillis = 2100L
            )
        )

        coEvery { galleryPhotoDao.insertAll(any()) } just runs

        repository.saveGalleryPhotos(photos)

        coVerify(exactly = 1) {
            galleryPhotoDao.insertAll(
                listOf(
                    GalleryPhotoEntity(
                        uri = "content://gallery/photo1",
                        displayName = "photo_1",
                        dateTakenMillis = 1000L,
                        addedAtMillis = 1100L
                    ),
                    GalleryPhotoEntity(
                        uri = "content://gallery/photo2",
                        displayName = "photo_2",
                        dateTakenMillis = 2000L,
                        addedAtMillis = 2100L
                    )
                )
            )
        }
    }

    @Test
    fun `saveGalleryPhotos should insert empty list when input is empty`() = runTest {
        coEvery { galleryPhotoDao.insertAll(emptyList()) } just runs

        repository.saveGalleryPhotos(emptyList())

        coVerify(exactly = 1) {
            galleryPhotoDao.insertAll(emptyList())
        }
    }
}