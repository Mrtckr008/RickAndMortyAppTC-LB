package com.interview.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey
    val characterId: Int,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?
)