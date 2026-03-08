package com.interview.data.di

import android.content.Context
import androidx.room.Room
import com.interview.data.local.db.AppDatabase
import com.interview.data.local.db.AppDatabase.Companion.DATABASE_NAME
import com.interview.data.local.dao.CharacterDao
import com.interview.data.local.dao.GalleryPhotoDao
import com.interview.data.local.dao.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideCharacterDao(database: AppDatabase): CharacterDao {
        return database.characterDao()
    }

    @Provides
    fun provideRemoteKeysDao(database: AppDatabase): RemoteKeysDao {
        return database.remoteKeysDao()
    }

    @Provides
    fun provideGalleryPhotoDao(database: AppDatabase): GalleryPhotoDao {
        return database.galleryPhotoDao()
    }
}