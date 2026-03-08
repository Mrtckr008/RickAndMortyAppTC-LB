package com.interview.rickandmortyturkcell.ui.characterdetail.model

import com.interview.domain.model.Character
import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus

data class CharacterDetailUiState(
    val isLoading: Boolean = true,
    val character: CharacterDetailModel? = null
)

data class CharacterDetailModel(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val status: CharacterStatus,
    val species: CharacterSpecies,
    val gender: CharacterGender,
    val originName: String,
    val locationName: String
)

fun Character.toDetailModel(): CharacterDetailModel {
    return CharacterDetailModel(
        id = id,
        name = name,
        imageUrl = image,
        status = status,
        species = species,
        gender = gender,
        originName = origin.name,
        locationName = location.name
    )
}