package com.interview.data.mapper

import com.interview.data.local.entity.CharacterEntity
import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterDomainMapperTest {

    @Test
    fun `toDomain should map entity to domain correctly`() {
        val entity = CharacterEntity(
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
            createdAtMillis = 1509834329008L,
            page = 1
        )

        val result = entity.toDomain()

        assertEquals(18, result.id)
        assertEquals("Antenna Morty", result.name)
        assertEquals(CharacterStatus.ALIVE, result.status)
        assertEquals(CharacterSpecies.HUMAN, result.species)
        assertEquals("Human with antennae", result.type)
        assertEquals(CharacterGender.MALE, result.gender)

        assertEquals("Earth (Replacement Dimension)", result.origin.name)
        assertEquals("https://rickandmortyapi.com/api/location/20", result.origin.url)

        assertEquals("Citadel of Ricks", result.location.name)
        assertEquals("https://rickandmortyapi.com/api/location/3", result.location.url)

        assertEquals("https://rickandmortyapi.com/api/character/avatar/18.jpeg", result.image)
        assertEquals(
            listOf(
                "https://rickandmortyapi.com/api/episode/10",
                "https://rickandmortyapi.com/api/episode/28"
            ), result.episodes
        )
        assertEquals("https://rickandmortyapi.com/api/character/18", result.url)
        assertEquals("2017-11-04T22:25:29.008Z", result.created)
    }

    @Test
    fun `toDomain should keep empty string and empty list values`() {
        val entity = CharacterEntity(
            id = 2,
            name = "",
            status = "unknown",
            species = "unknown",
            type = "",
            gender = "unknown",
            originName = "",
            originUrl = "",
            locationName = "",
            locationUrl = "",
            image = "",
            episodeUrls = emptyList(),
            url = "",
            created = "",
            createdAtMillis = 0L,
            page = 1
        )

        val result = entity.toDomain()

        assertEquals("", result.name)
        assertEquals(CharacterStatus.UNKNOWN, result.status)
        assertEquals(CharacterSpecies.UNKNOWN, result.species)
        assertEquals("", result.type)
        assertEquals(CharacterGender.UNKNOWN, result.gender)
        assertEquals("", result.origin.name)
        assertEquals("", result.origin.url)
        assertEquals("", result.location.name)
        assertEquals("", result.location.url)
        assertEquals("", result.image)
        assertEquals(emptyList<String>(), result.episodes)
        assertEquals("", result.url)
        assertEquals("", result.created)
    }
}