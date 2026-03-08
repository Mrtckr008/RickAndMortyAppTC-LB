package com.interview.domain.usecase

import com.interview.domain.model.GalleryPhoto
import com.interview.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGalleryPhotosUseCase @Inject constructor(
    private val repository: GalleryRepository
) {
    operator fun invoke(isNewestFirst: Boolean): Flow<List<GalleryPhoto>> {
        return repository.observeGalleryPhotos(isNewestFirst)
    }
}