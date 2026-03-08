package com.interview.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.interview.data.local.dao.CharacterDao
import com.interview.data.local.db.AppDatabase
import com.interview.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var characterDao: CharacterDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build()

        characterDao = database.characterDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeCharacterById_shouldReturnCharacter_whenExists() = runTest {
        val entity = characterEntity(
            id = 1, name = "Rick Sanchez"
        )

        characterDao.insertAll(listOf(entity))

        val result = characterDao.observeCharacterById(1).first()

        assertEquals(entity.id, result?.id)
        assertEquals(entity.name, result?.name)
        assertEquals(entity.image, result?.image)
    }

    @Test
    fun observeCharacterById_shouldReturnNull_whenCharacterDoesNotExist() = runTest {
        val result = characterDao.observeCharacterById(999).first()

        assertNull(result)
    }

    @Test
    fun insertAll_shouldInsertCharacters() = runTest {
        val entities = listOf(
            characterEntity(id = 1, name = "Rick Sanchez"),
            characterEntity(id = 2, name = "Morty Smith")
        )

        characterDao.insertAll(entities)

        val first = characterDao.observeCharacterById(1).first()
        val second = characterDao.observeCharacterById(2).first()

        assertEquals("Rick Sanchez", first?.name)
        assertEquals("Morty Smith", second?.name)
    }

    @Test
    fun insertAll_shouldReplaceCharacter_whenSameIdInsertedAgain() = runTest {
        val oldEntity = characterEntity(id = 1, name = "Old Rick")
        val newEntity = characterEntity(id = 1, name = "New Rick")

        characterDao.insertAll(listOf(oldEntity))
        characterDao.insertAll(listOf(newEntity))

        val result = characterDao.observeCharacterById(1).first()

        assertEquals("New Rick", result?.name)
    }

    @Test
    fun clearAll_shouldDeleteAllCharacters() = runTest {
        val entities = listOf(
            characterEntity(id = 1, name = "Rick Sanchez"),
            characterEntity(id = 2, name = "Morty Smith")
        )

        characterDao.insertAll(entities)
        characterDao.clearAll()

        val first = characterDao.observeCharacterById(1).first()
        val second = characterDao.observeCharacterById(2).first()

        assertNull(first)
        assertNull(second)
    }

    private fun characterEntity(
        id: Int, name: String
    ): CharacterEntity {
        return CharacterEntity(
            id = id,
            name = name,
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            originName = "Earth (C-137)",
            originUrl = "",
            locationName = "Citadel of Ricks",
            locationUrl = "",
            image = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
            episodeUrls = emptyList(),
            url = "https://rickandmortyapi.com/api/character/$id",
            created = "2017-11-04T18:48:46.250Z",
            page = 1,
            createdAtMillis = 1509821326250L
        )
    }
}