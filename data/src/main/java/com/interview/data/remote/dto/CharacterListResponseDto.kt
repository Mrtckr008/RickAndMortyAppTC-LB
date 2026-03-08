package com.interview.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CharacterListResponseDto(
    @SerializedName("info")
    val info: InfoDto?,
    @SerializedName("results")
    val results: List<CharacterDto>?
)