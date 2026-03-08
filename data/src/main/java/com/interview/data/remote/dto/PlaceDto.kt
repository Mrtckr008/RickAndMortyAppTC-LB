package com.interview.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PlaceDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("url")
    val url: String?
)