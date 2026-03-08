package com.interview.data.mapper

import com.interview.data.local.entity.CharacterEntity
import com.interview.domain.model.Character
import com.interview.domain.model.CharacterPlace

fun CharacterEntity.toDomain(): Character {
    return Character(
        id = id,
        name = name,
        status = status.toCharacterStatus(),
        species = species.toCharacterSpecies(),
        type = type,
        gender = gender.toCharacterGender(),
        origin = CharacterPlace(
            name = originName, url = originUrl
        ),
        location = CharacterPlace(
            name = locationName, url = locationUrl
        ),
        image = image,
        episodes = episodeUrls,
        url = url,
        created = created
    )
}
