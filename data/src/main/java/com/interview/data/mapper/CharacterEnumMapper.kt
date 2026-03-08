package com.interview.data.mapper

import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus

fun String?.toCharacterStatus(): CharacterStatus {
    return when (this?.trim()?.lowercase()) {
        "alive" -> CharacterStatus.ALIVE
        "dead" -> CharacterStatus.DEAD
        else -> CharacterStatus.UNKNOWN
    }
}

fun String?.toCharacterSpecies(): CharacterSpecies {
    return when (this?.trim()?.lowercase()) {
        "human" -> CharacterSpecies.HUMAN
        "alien" -> CharacterSpecies.ALIEN
        "unknown" -> CharacterSpecies.UNKNOWN
        null, "" -> CharacterSpecies.UNKNOWN
        else -> CharacterSpecies.UNKNOWN
    }
}

fun String?.toCharacterGender(): CharacterGender {
    return when (this?.trim()?.lowercase()) {
        "male" -> CharacterGender.MALE
        "female" -> CharacterGender.FEMALE
        "genderless" -> CharacterGender.GENDERLESS
        else -> CharacterGender.UNKNOWN
    }
}