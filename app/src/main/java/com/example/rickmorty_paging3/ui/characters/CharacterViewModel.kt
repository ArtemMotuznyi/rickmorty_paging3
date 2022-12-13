package com.example.rickmorty_paging3.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.rickmorty_paging3.api.ApiService
import com.example.rickmorty_paging3.paging.RickyMortyPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val apiService: ApiService
): ViewModel() {

    val listData = Pager(PagingConfig(pageSize = 1)) {
        RickyMortyPagingSource(apiService)
    }.flow.cachedIn(viewModelScope)
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