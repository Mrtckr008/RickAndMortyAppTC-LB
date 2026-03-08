package com.interview.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.interview.data.local.dao.GalleryPhotoDao
import com.interview.data.local.db.AppDatabase
import com.interview.data.local.entity.GalleryPhotoEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryPhotoDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: GalleryPhotoDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.galleryPhotoDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeGalleryPhotos_whenSortDescendingTrue_should_return_newest_first() = runTest {
        dao.insertAll(
            listOf(
                galleryPhoto(
                    uri = "content://gallery/1",
                    displayName = "photo_1",
                    dateTakenMillis = 1000L,
                    addedAtMillis = 1100L
                ), galleryPhoto(
                    uri = "content://gallery/2",
                    displayName = "photo_2",
                    dateTakenMillis = 3000L,
                    addedAtMillis = 3100L
                ), galleryPhoto(
                    uri = "content://gallery/3",
                    displayName = "photo_3",
                    dateTakenMillis = 2000L,
                    addedAtMillis = 2100L
                )
            )
        )

        dao.observeGalleryPhotos(sortDescending = true).test {
            val result = awaitItem()

            assertEquals(
                listOf(
                    "content://gallery/2",
                    "content://gallery/3",
                    "content://gallery/1"
                ), result.map { it.uri })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeGalleryPhotos_whenSortDescendingFalse_should_return_oldest_first() = runTest {
        dao.insertAll(
            listOf(
                galleryPhoto(
                    uri = "content://gallery/1",
                    displayName = "photo_1",
                    dateTakenMillis = 1000L,
                    addedAtMillis = 1100L
                ), galleryPhoto(
                    uri = "content://gallery/2",
                    displayName = "photo_2",
                    dateTakenMillis = 3000L,
                    addedAtMillis = 3100L
                ), galleryPhoto(
                    uri = "content://gallery/3",
                    displayName = "photo_3",
                    dateTakenMillis = 2000L,
                    addedAtMillis = 2100L
                )
            )
        )

        dao.observeGalleryPhotos(sortDescending = false).test {
            val result = awaitItem()

            assertEquals(
                listOf(
                    "content://gallery/1",
                    "content://gallery/3",
                    "content://gallery/2"
                ), result.map { it.uri })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeGalleryPhotos_should_use_addedAtMillis_desc_as_tie_breaker() = runTest {
        dao.insertAll(
            listOf(
                galleryPhoto(
                    uri = "content://gallery/1",
                    displayName = "photo_1",
                    dateTakenMillis = 2000L,
                    addedAtMillis = 2100L
                ), galleryPhoto(
                    uri = "content://gallery/2",
                    displayName = "photo_2",
                    dateTakenMillis = 2000L,
                    addedAtMillis = 2500L
                ), galleryPhoto(
                    uri = "content://gallery/3",
                    displayName = "photo_3",
                    dateTakenMillis = 2000L,
                    addedAtMillis = 2300L
                )
            )
        )

        dao.observeGalleryPhotos(sortDescending = true).test {
            val result = awaitItem()

            assertEquals(
                listOf("content://gallery/2", "content://gallery/3", "content://gallery/1"),
                result.map { it.uri })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertAll_withSameUri_should_ignore_duplicate() = runTest {
        dao.insertAll(
            listOf(
                galleryPhoto(
                    uri = "content://gallery/1",
                    displayName = "photo_old",
                    dateTakenMillis = 1000L,
                    addedAtMillis = 1100L
                )
            )
        )

        dao.insertAll(
            listOf(
                galleryPhoto(
                    uri = "content://gallery/1",
                    displayName = "photo_new",
                    dateTakenMillis = 9999L,
                    addedAtMillis = 9999L
                )
            )
        )

        dao.observeGalleryPhotos(sortDescending = true).test {
            val result = awaitItem()

            assertEquals(1, result.size)
            assertEquals("photo_old", result.first().displayName)
            assertEquals(1000L, result.first().dateTakenMillis)
            assertEquals(1100L, result.first().addedAtMillis)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteByUri_should_remove_matching_item() = runTest {
        dao.insertAll(
            listOf(
                galleryPhoto(
                    uri = "content://gallery/1",
                    displayName = "photo_1",
                    dateTakenMillis = 1000L,
                    addedAtMillis = 1100L
                ), galleryPhoto(
                    uri = "content://gallery/2",
                    displayName = "photo_2",
                    dateTakenMillis = 2000L,
                    addedAtMillis = 2100L
                )
            )
        )

        dao.deleteByUri("content://gallery/1")

        dao.observeGalleryPhotos(sortDescending = true).test {
            val result = awaitItem()

            assertEquals(1, result.size)
            assertEquals("content://gallery/2", result.first().uri)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun galleryPhoto(
        uri: String, displayName: String?, dateTakenMillis: Long?, addedAtMillis: Long
    ): GalleryPhotoEntity {
        return GalleryPhotoEntity(
            uri = uri,
            displayName = displayName,
            dateTakenMillis = dateTakenMillis,
            addedAtMillis = addedAtMillis
        )
    }
}