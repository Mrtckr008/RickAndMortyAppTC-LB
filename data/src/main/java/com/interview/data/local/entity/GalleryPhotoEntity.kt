package com.interview.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gallery_photos")
data class GalleryPhotoEntity(
    @PrimaryKey
    val uri: String,
    val displayName: String?,
    val dateTakenMillis: Long?,
    val addedAtMillis: Long
)