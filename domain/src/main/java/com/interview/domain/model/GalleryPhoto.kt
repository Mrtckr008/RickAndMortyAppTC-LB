package com.interview.domain.model

data class GalleryPhoto(
    val uri: String,
    val displayName: String?,
    val dateTakenMillis: Long?,
    val addedAtMillis: Long
)