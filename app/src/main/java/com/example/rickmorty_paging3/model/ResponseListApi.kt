package com.example.rickmorty_paging3.model

data class ResponseListApi(
    val results: List<RickMorty>
)

data class ResponseCountApi(
    val info: InfoData
)