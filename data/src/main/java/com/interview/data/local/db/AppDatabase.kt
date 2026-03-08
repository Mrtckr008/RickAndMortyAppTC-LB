package com.interview.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.interview.data.local.entity.CharacterEntity
import com.interview.data.local.entity.RemoteKeysEntity
import com.interview.data.local.dao.CharacterDao
import com.interview.data.local.dao.RemoteKeysDao
import com.interview.data.local.converter.Converters
import com.interview.data.local.dao.GalleryPhotoDao
import com.interview.data.local.entity.GalleryPhotoEntity

@Database(
    entities = [
        CharacterEntity::class,
        RemoteKeysEntity::class,
        GalleryPhotoEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun characterDao(): CharacterDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun galleryPhotoDao(): GalleryPhotoDao

    companion object {
        const val DATABASE_NAME = "rick_and_morty.db"
    }
}