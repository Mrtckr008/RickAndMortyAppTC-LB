package com.interview.domain.model

data class Character(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: CharacterSpecies,
    val type: String,
    val gender: CharacterGender,
    val origin: CharacterPlace,
    val location: CharacterPlace,
    val image: String,
    val episodes: List<String>,
    val url: String,
    val created: String
)