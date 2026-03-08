package com.interview.data.remote.api

import com.interview.data.remote.dto.CharacterListResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CharacterApiService {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int
    ): CharacterListResponseDto
}