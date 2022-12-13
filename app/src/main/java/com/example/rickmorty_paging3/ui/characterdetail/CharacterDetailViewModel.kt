package com.example.rickmorty_paging3.ui.characterdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.rickmorty_paging3.model.RickMorty
import com.example.rickmorty_paging3.utils.Resource
import com.example.rickmorty_paging3.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val repository: CharacterRepository
): ViewModel() {

    private val _id = MutableLiveData<Int>()

    private val _character = _id.switchMap { id ->
        repository.getCharacter(id)
    }

    val character: LiveData<Resource<RickMorty>> = _character

    fun start(id: Int) {
        _id.value = id
    }
}