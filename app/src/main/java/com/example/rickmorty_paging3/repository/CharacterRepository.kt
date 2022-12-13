package com.example.rickmorty_paging3.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.rickmorty_paging3.api.ApiService
import com.example.rickmorty_paging3.api.CharacterRemoteDataSource
import com.example.rickmorty_paging3.utils.Resource
import javax.inject.Inject
import com.example.rickmorty_paging3.utils.Resource.Status.*

class CharacterRepository @Inject constructor(
    private val remoteDataSource: CharacterRemoteDataSource
) {
    fun getCharacter(id: Int) = performGetOperation { remoteDataSource.getCharacter(id) }

    fun <T> performGetOperation(networkCall: suspend () -> Resource<T>): LiveData<Resource<T>> = liveData {
        emit(Resource.loading())

        val responseStatus = networkCall.invoke()
        if (responseStatus.status == SUCCESS) {
            emit(Resource.success(responseStatus.data!!))
        } else if (responseStatus.status == ERROR) {
            emit(Resource.error(responseStatus.message!!))
        }
    }
}