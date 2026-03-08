package com.interview.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.interview.data.local.dao.RemoteKeysDao
import com.interview.data.local.db.AppDatabase
import com.interview.data.local.entity.RemoteKeysEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteKeysDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: RemoteKeysDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.remoteKeysDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAll_and_remoteKeysByCharacterId_should_return_matching_item() = runTest {
        val keys = listOf(
            RemoteKeysEntity(
                characterId = 1, prevKey = null, currentPage = 1, nextKey = 2
            ), RemoteKeysEntity(
                characterId = 2, prevKey = 1, currentPage = 2, nextKey = 3
            )
        )

        dao.insertAll(keys)

        val result = dao.remoteKeysByCharacterId(2)

        assertEquals(
            RemoteKeysEntity(
                characterId = 2, prevKey = 1, currentPage = 2, nextKey = 3
            ), result
        )
    }

    @Test
    fun remoteKeysByCharacterId_should_return_null_when_item_not_found() = runTest {
        val result = dao.remoteKeysByCharacterId(999)

        assertNull(result)
    }

    @Test
    fun insertAll_should_replace_existing_item_with_same_characterId() = runTest {
        dao.insertAll(
            listOf(
                RemoteKeysEntity(
                    characterId = 10, prevKey = 1, currentPage = 2, nextKey = 3
                )
            )
        )

        dao.insertAll(
            listOf(
                RemoteKeysEntity(
                    characterId = 10, prevKey = 5, currentPage = 6, nextKey = 7
                )
            )
        )

        val result = dao.remoteKeysByCharacterId(10)

        assertEquals(
            RemoteKeysEntity(
                characterId = 10, prevKey = 5, currentPage = 6, nextKey = 7
            ), result
        )
    }

    @Test
    fun clearRemoteKeys_should_delete_all_items() = runTest {
        dao.insertAll(
            listOf(
                RemoteKeysEntity(
                    characterId = 1, prevKey = null, currentPage = 1, nextKey = 2
                ), RemoteKeysEntity(
                    characterId = 2, prevKey = 1, currentPage = 2, nextKey = 3
                )
            )
        )

        dao.clearRemoteKeys()

        assertNull(dao.remoteKeysByCharacterId(1))
        assertNull(dao.remoteKeysByCharacterId(2))
    }
}