package com.example.rickmorty_paging3.ui.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.pagingselectionlib.PagingDataSelection
import com.example.rickmorty_paging3.api.ApiService
import com.example.rickmorty_paging3.api.CharacterRemoteDataSource
import com.example.rickmorty_paging3.model.InfoData
import com.example.rickmorty_paging3.model.RickMorty
import com.example.rickmorty_paging3.paging.RickyMortyPagingSource
import com.example.rickmorty_paging3.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val apiService: ApiService,
    private val characterRemoteDataSource: CharacterRemoteDataSource
): ViewModel() {

    val listData = Pager(PagingConfig(pageSize = 1)) {
        RickyMortyPagingSource(apiService)
    }.flow.cachedIn(viewModelScope)

    private val charactersCount = performGetOperation { characterRemoteDataSource.getCharactersCount() }.map {
        it.data?.info?.count
    }

    val selectionLiveData = MutableSharedFlow<PagingDataSelection<RickMorty>>()
    val selectedCountToAllCount: Flow<Pair<Int?, Int>?> = selectionLiveData.combine(charactersCount) { selection, count->
        count?.let { charactersCount->
            when(selection.state){
                PagingDataSelection.State.SELECTION -> selection.items.size to charactersCount
                PagingDataSelection.State.SELECTED_ALL -> charactersCount to charactersCount
                PagingDataSelection.State.DESELECTION -> charactersCount - selection.items.size to charactersCount
                else -> null to charactersCount
            }
        }
    }

    fun setSelection(selection: PagingDataSelection<RickMorty>){
        viewModelScope.launch {
            selectionLiveData.emit(selection)
        }
    }

    fun <T> performGetOperation(networkCall: suspend () -> Resource<T>): Flow<Resource<T>> = flow {
        emit(Resource.loading())

        val responseStatus = networkCall.invoke()
        if (responseStatus.status == Resource.Status.SUCCESS) {
            emit(Resource.success(responseStatus.data!!))
        } else if (responseStatus.status == Resource.Status.ERROR) {
            emit(Resource.error(responseStatus.message!!))
        }
    }

}


/*
ViewModel
- is designed to store and manage UI-related data in a lifecycle conscious way

@HiltViewModel
- To enable injection a ViewModel by Hilt

@Inject
- to perform field injection

Pager
- to set up reactive stream
- a class responsible for producing PagingData stream from PagingSource implementation
- it should be created in ViewModel class

PagingConfig
- a class that defines parameters that determine paging behavior
 */