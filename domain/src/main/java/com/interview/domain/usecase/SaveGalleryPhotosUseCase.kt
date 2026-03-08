package com.interview.domain.usecase

import com.interview.domain.model.GalleryPhoto
import com.interview.domain.repository.GalleryRepository
import javax.inject.Inject

class SaveGalleryPhotosUseCase @Inject constructor(
    private val repository: GalleryRepository
) {
    suspend operator fun invoke(items: List<GalleryPhoto>) {
        repository.saveGalleryPhotos(items)
    }
}