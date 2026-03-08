package com.interview.data.di

import com.interview.data.repository.CharacterRepositoryImpl
import com.interview.data.repository.GalleryRepositoryImpl
import com.interview.domain.repository.CharacterRepository
import com.interview.domain.repository.GalleryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        impl: CharacterRepositoryImpl
    ): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindGalleryRepository(
        impl: GalleryRepositoryImpl
    ): GalleryRepository
}