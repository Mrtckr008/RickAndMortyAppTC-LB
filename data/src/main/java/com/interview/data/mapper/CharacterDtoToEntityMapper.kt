package com.interview.data.mapper

import com.interview.data.local.entity.CharacterEntity
import com.interview.data.local.entity.RemoteKeysEntity
import com.interview.data.remote.dto.CharacterDto
import java.time.Instant

fun CharacterDto.toEntity(page: Int): CharacterEntity {
    return CharacterEntity(
        id = id ?: 0,
        name = name.orEmpty(),
        status = status.orEmpty(),
        species = species.orEmpty(),
        type = type.orEmpty(),
        gender = gender.orEmpty(),
        originName = origin?.name.orEmpty(),
        originUrl = origin?.url.orEmpty(),
        locationName = location?.name.orEmpty(),
        locationUrl = location?.url.orEmpty(),
        image = image.orEmpty(),
        episodeUrls = episode.orEmpty(),
        url = url.orEmpty(),
        created = created.orEmpty(),
        page = page,
        createdAtMillis = created.toEpochMillis(),
    )
}

fun CharacterDto.toRemoteKeys(
    page: Int, prevKey: Int?, nextKey: Int?
): RemoteKeysEntity {
    return RemoteKeysEntity(
        characterId = id ?: 0, prevKey = prevKey, currentPage = page, nextKey = nextKey
    )
}

private fun String?.toEpochMillis(): Long {
    return runCatching {
        Instant.parse(this.orEmpty()).toEpochMilli()
    }.getOrDefault(0L)
}
