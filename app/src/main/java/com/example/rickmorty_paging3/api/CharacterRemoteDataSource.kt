package com.example.rickmorty_paging3.api

import javax.inject.Inject

class CharacterRemoteDataSource @Inject constructor(
    private val apiService: ApiService
): BaseDataResource() {

    suspend fun getCharacter(id: Int) = getResult { apiService.getCharacter(id) }
}