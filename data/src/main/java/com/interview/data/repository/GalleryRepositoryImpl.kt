package com.interview.data.repository

import com.interview.data.di.IoDispatcher
import com.interview.data.local.dao.GalleryPhotoDao
import com.interview.data.local.entity.GalleryPhotoEntity
import com.interview.domain.model.GalleryPhoto
import com.interview.domain.repository.GalleryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(
    private val galleryPhotoDao: GalleryPhotoDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GalleryRepository {

    override fun observeGalleryPhotos(isNewestFirst: Boolean): Flow<List<GalleryPhoto>> {
        return galleryPhotoDao.observeGalleryPhotos(isNewestFirst)
            .map { entities -> entities.map(GalleryPhotoEntity::toDomain) }
    }

    override suspend fun saveGalleryPhotos(items: List<GalleryPhoto>) = withContext(ioDispatcher) {
        galleryPhotoDao.insertAll(items.map(GalleryPhoto::toEntity))
    }
}

private fun GalleryPhotoEntity.toDomain(): GalleryPhoto {
    return GalleryPhoto(
        uri = uri,
        displayName = displayName,
        dateTakenMillis = dateTakenMillis,
        addedAtMillis = addedAtMillis
    )
}

private fun GalleryPhoto.toEntity(): GalleryPhotoEntity {
    return GalleryPhotoEntity(
        uri = uri,
        displayName = displayName,
        dateTakenMillis = dateTakenMillis,
        addedAtMillis = addedAtMillis
    )
}
