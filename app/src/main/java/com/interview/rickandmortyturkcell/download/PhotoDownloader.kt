package com.interview.rickandmortyturkcell.download

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.interview.data.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun downloadToGallery(
        photoSource: String, photoName: String
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val resolver = context.contentResolver
            val sourceUri = photoSource.toUri()

            val mimeType = resolveMimeType(
                resolver = resolver, sourceUri = sourceUri, photoSource = photoSource
            )

            val fileName = buildFileName(
                displayName = photoName, source = photoSource, mimeType = mimeType
            )

            val destinationUri = createMediaStoreEntry(
                resolver = resolver, fileName = fileName, mimeType = mimeType
            )

            try {
                resolver.openOutputStream(destinationUri)?.use { outputStream ->
                    openSourceInputStream(
                        resolver = resolver, sourceUri = sourceUri, photoSource = photoSource
                    ).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                } ?: throw IOException("Failed to open output stream for destination uri")
            } catch (e: Exception) {
                resolver.delete(destinationUri, null, null)
                throw e
            }

            markDownloadCompletedIfNeeded(
                resolver = resolver, destinationUri = destinationUri
            )
        }
    }

    private fun resolveMimeType(
        resolver: android.content.ContentResolver, sourceUri: Uri, photoSource: String
    ): String {
        return resolver.getType(sourceUri) ?: guessMimeTypeFromSource(photoSource)
        ?: DEFAULT_MIME_TYPE
    }

    private fun createMediaStoreEntry(
        resolver: android.content.ContentResolver, fileName: String, mimeType: String
    ): Uri {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.RELATIVE_PATH, GALLERY_RELATIVE_PATH)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: throw IOException("Failed to create MediaStore record")
    }

    private fun markDownloadCompletedIfNeeded(
        resolver: android.content.ContentResolver, destinationUri: Uri
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            resolver.update(destinationUri, values, null, null)
        }
    }

    private fun openSourceInputStream(
        resolver: android.content.ContentResolver, sourceUri: Uri, photoSource: String
    ): InputStream {
        return when (sourceUri.scheme?.lowercase(Locale.ROOT)) {
            SCHEME_CONTENT -> {
                resolver.openInputStream(sourceUri)
                    ?: throw IOException("Failed to open input stream from content uri")
            }

            SCHEME_HTTP, SCHEME_HTTPS -> {
                downloadFromNetwork(photoSource)
            }

            else -> {
                throw IOException("Unsupported photo source: $photoSource")
            }
        }
    }

    private fun downloadFromNetwork(imageUrl: String): InputStream {
        val connection = (URL(imageUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = NETWORK_TIMEOUT_MS
            readTimeout = NETWORK_TIMEOUT_MS
            requestMethod = REQUEST_METHOD_GET
            doInput = true
            connect()
        }

        if (connection.responseCode !in HTTP_SUCCESS_RANGE) {
            connection.disconnect()
            throw IOException("Image download failed: ${connection.responseCode}")
        }

        val networkInputStream = connection.inputStream

        return object : InputStream() {
            override fun read(): Int = networkInputStream.read()

            override fun read(b: ByteArray): Int = networkInputStream.read(b)

            override fun read(b: ByteArray, off: Int, len: Int): Int {
                return networkInputStream.read(b, off, len)
            }

            override fun close() {
                try {
                    networkInputStream.close()
                } finally {
                    connection.disconnect()
                }
            }
        }
    }

    private fun guessMimeTypeFromSource(source: String): String? {
        return when (extractExtension(source)) {
            EXT_JPG, EXT_JPEG -> MIME_JPEG
            EXT_PNG -> MIME_PNG
            EXT_WEBP -> MIME_WEBP
            else -> null
        }
    }

    private fun buildFileName(
        displayName: String, source: String, mimeType: String
    ): String {
        val safeName = sanitizeFileName(displayName).ifBlank { DEFAULT_FILE_NAME }

        val extension = when (mimeType) {
            MIME_PNG -> EXT_PNG
            MIME_WEBP -> EXT_WEBP
            MIME_JPEG -> EXT_JPG
            else -> extractExtension(source).ifBlank { EXT_JPG }
        }

        return "${safeName}_${System.currentTimeMillis()}.$extension"
    }

    private fun sanitizeFileName(input: String): String {
        return input.trim().replace(FILE_NAME_INVALID_CHARS_REGEX, "")
            .replace(WHITESPACE_REGEX, "_").trim('_')
    }

    private fun extractExtension(source: String): String {
        return source.substringAfterLast('.', "").substringBefore('?').substringBefore('#')
            .lowercase(Locale.ROOT)
    }

    private companion object {
        const val GALLERY_RELATIVE_PATH = "Pictures/RickAndMortyTurkcell"

        const val DEFAULT_FILE_NAME = "character_photo"
        const val DEFAULT_MIME_TYPE = "image/jpeg"

        const val MIME_JPEG = "image/jpeg"
        const val MIME_PNG = "image/png"
        const val MIME_WEBP = "image/webp"

        const val EXT_JPG = "jpg"
        const val EXT_JPEG = "jpeg"
        const val EXT_PNG = "png"
        const val EXT_WEBP = "webp"

        const val SCHEME_CONTENT = "content"
        const val SCHEME_HTTP = "http"
        const val SCHEME_HTTPS = "https"

        const val REQUEST_METHOD_GET = "GET"
        const val NETWORK_TIMEOUT_MS = 15_000

        val HTTP_SUCCESS_RANGE = 200..299
        val FILE_NAME_INVALID_CHARS_REGEX = Regex("[\\\\/:*?\"<>|]")
        val WHITESPACE_REGEX = Regex("\\s+")
    }
}
