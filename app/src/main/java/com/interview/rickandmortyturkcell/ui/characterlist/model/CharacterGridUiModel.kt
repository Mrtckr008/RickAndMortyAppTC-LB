package com.interview.rickandmortyturkcell.ui.characterlist.model

import com.interview.domain.model.CharacterGender
import com.interview.domain.model.CharacterSpecies
import com.interview.domain.model.CharacterStatus

sealed interface CharacterGridUiModel {

    val uniqueKey: String

    data class CharacterItem(
        val id: Int,
        val name: String,
        val imageUrl: String,
        val gender: CharacterGender,
        val species: CharacterSpecies,
        val status: CharacterStatus,
        override val uniqueKey: String = "character_$id"
    ) : CharacterGridUiModel

    data class GalleryItem(
        val uri: String,
        val displayName: String?,
        val dateTakenMillis: Long?,
        override val uniqueKey: String = "gallery_$uri"
    ) : CharacterGridUiModel
}