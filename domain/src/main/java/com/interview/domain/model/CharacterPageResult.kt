package com.interview.domain.model

data class CharacterPageResult(
    val items: List<Character>,
    val nextPage: Int?,
    val endReached: Boolean
)