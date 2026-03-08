package com.interview.rickandmortyturkcell.photodetail

import androidx.lifecycle.SavedStateHandle
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.interview.rickandmortyturkcell.MainDispatcherRule
import com.interview.rickandmortyturkcell.R
import com.interview.rickandmortyturkcell.analytics.AppAnalytics
import com.interview.rickandmortyturkcell.download.PhotoDownloader
import com.interview.rickandmortyturkcell.ui.photodetail.PHOTO_NAME
import com.interview.rickandmortyturkcell.ui.photodetail.PHOTO_URL
import com.interview.rickandmortyturkcell.ui.photodetail.PhotoDetailViewModel
import com.interview.rickandmortyturkcell.ui.photodetail.model.UiMessage
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.net.URLEncoder

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val photoDownloader: PhotoDownloader = mockk()
    private val appAnalytics: AppAnalytics = mockk(relaxed = true)

    @Test
    fun `uiState should decode photoUrl and photoName from savedStateHandle`() {
        val encodedUrl = URLEncoder.encode("https://example.com/rick.jpg", "UTF-8")
        val encodedName = URLEncoder.encode("Rick Sanchez", "UTF-8")

        val savedStateHandle = SavedStateHandle(
            mapOf(
                PHOTO_URL to encodedUrl, PHOTO_NAME to encodedName
            )
        )

        val viewModel = PhotoDetailViewModel(
            savedStateHandle = savedStateHandle,
            photoDownloader = photoDownloader,
            appAnalytics = appAnalytics
        )

        assertEquals("https://example.com/rick.jpg", viewModel.uiState.value.photoUrl)
        assertEquals("Rick Sanchez", viewModel.uiState.value.photoName)
        assertFalse(viewModel.uiState.value.isDownloading)
        assertNull(viewModel.uiState.value.downloadMessage)
    }

    @Test
    fun `downloadPhoto should update state and log analytics when download succeeds`() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                PHOTO_URL to URLEncoder.encode("https://example.com/rick.jpg", "UTF-8"),
                PHOTO_NAME to URLEncoder.encode("Rick Sanchez", "UTF-8")
            )
        )

        coEvery {
            photoDownloader.downloadToGallery(
                photoSource = "https://example.com/rick.jpg", photoName = "Rick Sanchez"
            )
        } returns Result.success(Unit)

        val viewModel = PhotoDetailViewModel(
            savedStateHandle = savedStateHandle,
            photoDownloader = photoDownloader,
            appAnalytics = appAnalytics
        )

        viewModel.downloadPhoto()
        advanceUntilIdle()

        coVerify(exactly = 1) {
            photoDownloader.downloadToGallery(
                photoSource = "https://example.com/rick.jpg", photoName = "Rick Sanchez"
            )
        }

        verify(exactly = 1) {
            appAnalytics.logPhotoDownloaded("Rick Sanchez")
        }

        assertFalse(viewModel.uiState.value.isDownloading)
        assertEquals(
            UiMessage.Resource(R.string.photo_saved_to_gallery),
            viewModel.uiState.value.downloadMessage
        )
    }

    @Test
    fun `downloadPhoto should update error resource message when download fails`() = runTest {
        mockkStatic(FirebaseCrashlytics::class)
        val crashlytics: FirebaseCrashlytics = mockk(relaxed = true)

        every { FirebaseCrashlytics.getInstance() } returns crashlytics
        every { crashlytics.recordException(any()) } just Runs

        val savedStateHandle = SavedStateHandle(
            mapOf(
                PHOTO_URL to URLEncoder.encode("https://example.com/rick.jpg", "UTF-8"),
                PHOTO_NAME to URLEncoder.encode("Rick Sanchez", "UTF-8")
            )
        )

        val exception = IllegalStateException("Download failed badly")

        coEvery {
            photoDownloader.downloadToGallery(
                photoSource = "https://example.com/rick.jpg", photoName = "Rick Sanchez"
            )
        } returns Result.failure(exception)

        val viewModel = PhotoDetailViewModel(
            savedStateHandle = savedStateHandle,
            photoDownloader = photoDownloader,
            appAnalytics = appAnalytics
        )

        viewModel.downloadPhoto()
        advanceUntilIdle()

        coVerify(exactly = 1) {
            photoDownloader.downloadToGallery(
                photoSource = "https://example.com/rick.jpg", photoName = "Rick Sanchez"
            )
        }

        verify(exactly = 0) {
            appAnalytics.logPhotoDownloaded(any())
        }

        verify(exactly = 1) {
            crashlytics.recordException(exception)
        }

        assertFalse(viewModel.uiState.value.isDownloading)
        assertEquals(
            UiMessage.Resource(R.string.download_failed), viewModel.uiState.value.downloadMessage
        )

        unmockkStatic(FirebaseCrashlytics::class)
    }

    @Test
    fun `consumeMessage should clear downloadMessage`() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                PHOTO_URL to URLEncoder.encode("https://example.com/rick.jpg", "UTF-8"),
                PHOTO_NAME to URLEncoder.encode("Rick Sanchez", "UTF-8")
            )
        )

        coEvery {
            photoDownloader.downloadToGallery(any(), any())
        } returns Result.success(Unit)

        val viewModel = PhotoDetailViewModel(
            savedStateHandle = savedStateHandle,
            photoDownloader = photoDownloader,
            appAnalytics = appAnalytics
        )

        viewModel.downloadPhoto()
        advanceUntilIdle()

        assertEquals(
            UiMessage.Resource(R.string.photo_saved_to_gallery),
            viewModel.uiState.value.downloadMessage
        )

        viewModel.consumeMessage()

        assertNull(viewModel.uiState.value.downloadMessage)
    }

    private fun assertFalse(value: Boolean) {
        assertEquals(false, value)
    }
}