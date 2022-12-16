package com.example.rickmorty_paging3.api

import com.example.rickmorty_paging3.model.ResponseCountApi
import com.example.rickmorty_paging3.model.ResponseListApi
import com.example.rickmorty_paging3.model.RickMorty
import com.example.rickmorty_paging3.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET(Constants.END_POINT)
    suspend fun getAllCharacters(@Query("page") page: Int): Response<ResponseListApi>

    @GET(Constants.END_POINT)
    suspend fun getCountCharacters(): Response<ResponseCountApi>

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): Response<RickMorty>

}