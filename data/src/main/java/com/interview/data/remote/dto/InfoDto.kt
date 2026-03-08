package com.interview.data.remote.dto

import com.google.gson.annotations.SerializedName

data class InfoDto(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("pages")
    val pages: Int?,
    @SerializedName("next")
    val next: String?,
    @SerializedName("prev")
    val prev: String?
)