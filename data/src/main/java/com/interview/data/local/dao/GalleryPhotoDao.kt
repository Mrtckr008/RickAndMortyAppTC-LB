package com.interview.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.interview.data.local.entity.GalleryPhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GalleryPhotoDao {

    @Query("""
        SELECT * FROM gallery_photos
        ORDER BY 
            CASE WHEN :sortDescending = 1 THEN dateTakenMillis END DESC,
            CASE WHEN :sortDescending = 0 THEN dateTakenMillis END ASC,
            addedAtMillis DESC
    """)
    fun observeGalleryPhotos(sortDescending: Boolean): Flow<List<GalleryPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<GalleryPhotoEntity>)

    @Query("DELETE FROM gallery_photos WHERE uri = :uri")
    suspend fun deleteByUri(uri: String)
}