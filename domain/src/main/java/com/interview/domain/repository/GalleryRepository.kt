package com.interview.domain.repository

import com.interview.domain.model.GalleryPhoto
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    fun observeGalleryPhotos(isNewestFirst: Boolean): Flow<List<GalleryPhoto>>
    suspend fun saveGalleryPhotos(items: List<GalleryPhoto>)
}