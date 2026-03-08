package com.interview.data.mapper

import com.interview.data.local.entity.CharacterEntity
import com.interview.data.local.entity.RemoteKeysEntity
import com.interview.data.remote.dto.CharacterDto
import com.interview.data.remote.dto.PlaceDto
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterEntityMapperTest {

    @Test
    fun `toEntity should map dto to entity correctly`() {
        val dto = CharacterDto(
            id = 18,
            name = "Antenna Morty",
            status = "Alive",
            species = "Human",
            type = "Human with antennae",
            gender = "Male",
            origin = PlaceDto(
                name = "Earth (Replacement Dimension)",
                url = "https://rickandmortyapi.com/api/location/20"
            ),
            location = PlaceDto(
                name = "Citadel of Ricks",
                url = "https://rickandmortyapi.com/api/location/3"
            ),
            image = "https://rickandmortyapi.com/api/character/avatar/18.jpeg",
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/10",
                "https://rickandmortyapi.com/api/episode/28"
            ),
            url = "https://rickandmortyapi.com/api/character/18",
            created = "2017-11-04T22:25:29.008Z"
        )

        val result = dto.toEntity(page = 2)

        assertEquals(
            CharacterEntity(
                id = 18,
                name = "Antenna Morty",
                status = "Alive",
                species = "Human",
                type = "Human with antennae",
                gender = "Male",
                originName = "Earth (Replacement Dimension)",
                originUrl = "https://rickandmortyapi.com/api/location/20",
                locationName = "Citadel of Ricks",
                locationUrl = "https://rickandmortyapi.com/api/location/3",
                image = "https://rickandmortyapi.com/api/character/avatar/18.jpeg",
                episodeUrls = listOf(
                    "https://rickandmortyapi.com/api/episode/10",
                    "https://rickandmortyapi.com/api/episode/28"
                ),
                url = "https://rickandmortyapi.com/api/character/18",
                created = "2017-11-04T22:25:29.008Z",
                page = 2,
                createdAtMillis = 1509834329008L
            ),
            result
        )
    }

    @Test
    fun `toEntity should use default values when dto fields are null`() {
        val dto = CharacterDto(
            id = null,
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null,
            origin = null,
            location = null,
            image = null,
            episode = null,
            url = null,
            created = null
        )

        val result = dto.toEntity(page = 1)

        assertEquals(0, result.id)
        assertEquals("", result.name)
        assertEquals("", result.status)
        assertEquals("", result.species)
        assertEquals("", result.type)
        assertEquals("", result.gender)
        assertEquals("", result.originName)
        assertEquals("", result.originUrl)
        assertEquals("", result.locationName)
        assertEquals("", result.locationUrl)
        assertEquals("", result.image)
        assertEquals(emptyList<String>(), result.episodeUrls)
        assertEquals("", result.url)
        assertEquals("", result.created)
        assertEquals(1, result.page)
        assertEquals(0L, result.createdAtMillis)
    }

    @Test
    fun `toEntity should set createdAtMillis to zero when created is invalid`() {
        val dto = CharacterDto(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = PlaceDto("Earth (C-137)", "https://rickandmortyapi.com/api/location/1"),
            location = PlaceDto("Citadel of Ricks", "https://rickandmortyapi.com/api/location/3"),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            episode = listOf("https://rickandmortyapi.com/api/episode/1"),
            url = "https://rickandmortyapi.com/api/character/1",
            created = "invalid-date"
        )

        val result = dto.toEntity(page = 1)

        assertEquals(0L, result.createdAtMillis)
    }

    @Test
    fun `toRemoteKeys should map dto to remote keys correctly`() {
        val dto = CharacterDto(
            id = 18,
            name = "Antenna Morty",
            status = "Alive",
            species = "Human",
            type = "Human with antennae",
            gender = "Male",
            origin = null,
            location = null,
            image = null,
            episode = null,
            url = null,
            created = null
        )

        val result = dto.toRemoteKeys(
            page = 3,
            prevKey = 2,
            nextKey = 4
        )

        assertEquals(
            RemoteKeysEntity(
                characterId = 18,
                prevKey = 2,
                currentPage = 3,
                nextKey = 4
            ),
            result
        )
    }

    @Test
    fun `toRemoteKeys should default characterId to zero when dto id is null`() {
        val dto = CharacterDto(
            id = null,
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null,
            origin = null,
            location = null,
            image = null,
            episode = null,
            url = null,
            created = null
        )

        val result = dto.toRemoteKeys(
            page = 1,
            prevKey = null,
            nextKey = 2
        )

        assertEquals(0, result.characterId)
        assertEquals(null, result.prevKey)
        assertEquals(1, result.currentPage)
        assertEquals(2, result.nextKey)
    }
}