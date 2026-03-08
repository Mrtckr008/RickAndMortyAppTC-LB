package com.interview.data.mapper

import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterEnumMapperTest {

    @Test
    fun `toCharacterStatus should return ALIVE for alive values`() {
        assertEquals(CharacterStatus.ALIVE, "alive".toCharacterStatus())
        assertEquals(CharacterStatus.ALIVE, "Alive".toCharacterStatus())
        assertEquals(CharacterStatus.ALIVE, " ALIVE ".toCharacterStatus())
    }

    @Test
    fun `toCharacterStatus should return DEAD for dead values`() {
        assertEquals(CharacterStatus.DEAD, "dead".toCharacterStatus())
        assertEquals(CharacterStatus.DEAD, "Dead".toCharacterStatus())
        assertEquals(CharacterStatus.DEAD, " DEAD ".toCharacterStatus())
    }

    @Test
    fun `toCharacterStatus should return UNKNOWN for null empty and unknown values`() {
        assertEquals(CharacterStatus.UNKNOWN, null.toCharacterStatus())
        assertEquals(CharacterStatus.UNKNOWN, "".toCharacterStatus())
        assertEquals(CharacterStatus.UNKNOWN, "   ".toCharacterStatus())
        assertEquals(CharacterStatus.UNKNOWN, "something".toCharacterStatus())
    }

    @Test
    fun `toCharacterSpecies should return HUMAN for human values`() {
        assertEquals(CharacterSpecies.HUMAN, "human".toCharacterSpecies())
        assertEquals(CharacterSpecies.HUMAN, "Human".toCharacterSpecies())
        assertEquals(CharacterSpecies.HUMAN, " HUMAN ".toCharacterSpecies())
    }

    @Test
    fun `toCharacterSpecies should return ALIEN for alien values`() {
        assertEquals(CharacterSpecies.ALIEN, "alien".toCharacterSpecies())
        assertEquals(CharacterSpecies.ALIEN, "Alien".toCharacterSpecies())
        assertEquals(CharacterSpecies.ALIEN, " ALIEN ".toCharacterSpecies())
    }

    @Test
    fun `toCharacterSpecies should return UNKNOWN for unknown null empty and unsupported values`() {
        assertEquals(CharacterSpecies.UNKNOWN, "unknown".toCharacterSpecies())
        assertEquals(CharacterSpecies.UNKNOWN, "Unknown".toCharacterSpecies())
        assertEquals(CharacterSpecies.UNKNOWN, null.toCharacterSpecies())
        assertEquals(CharacterSpecies.UNKNOWN, "".toCharacterSpecies())
        assertEquals(CharacterSpecies.UNKNOWN, "   ".toCharacterSpecies())
        assertEquals(CharacterSpecies.UNKNOWN, "robot".toCharacterSpecies())
    }

    @Test
    fun `toCharacterGender should return MALE for male values`() {
        assertEquals(CharacterGender.MALE, "male".toCharacterGender())
        assertEquals(CharacterGender.MALE, "Male".toCharacterGender())
        assertEquals(CharacterGender.MALE, " MALE ".toCharacterGender())
    }

    @Test
    fun `toCharacterGender should return FEMALE for female values`() {
        assertEquals(CharacterGender.FEMALE, "female".toCharacterGender())
        assertEquals(CharacterGender.FEMALE, "Female".toCharacterGender())
        assertEquals(CharacterGender.FEMALE, " FEMALE ".toCharacterGender())
    }

    @Test
    fun `toCharacterGender should return GENDERLESS for genderless values`() {
        assertEquals(CharacterGender.GENDERLESS, "genderless".toCharacterGender())
        assertEquals(CharacterGender.GENDERLESS, "Genderless".toCharacterGender())
        assertEquals(CharacterGender.GENDERLESS, " GENDERLESS ".toCharacterGender())
    }

    @Test
    fun `toCharacterGender should return UNKNOWN for null empty and unsupported values`() {
        assertEquals(CharacterGender.UNKNOWN, null.toCharacterGender())
        assertEquals(CharacterGender.UNKNOWN, "".toCharacterGender())
        assertEquals(CharacterGender.UNKNOWN, "   ".toCharacterGender())
        assertEquals(CharacterGender.UNKNOWN, "other".toCharacterGender())
    }
}