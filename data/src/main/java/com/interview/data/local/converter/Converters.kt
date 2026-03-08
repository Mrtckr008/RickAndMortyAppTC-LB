package com.interview.data.local.converter

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromEpisodeList(value: List<String>): String {
        return value.joinToString(separator = "||")
    }

    @TypeConverter
    fun toEpisodeList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return value.split("||")
    }
}