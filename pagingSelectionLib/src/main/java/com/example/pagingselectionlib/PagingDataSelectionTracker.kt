package com.example.pagingselectionlib

@FunctionalInterface
interface PagingDataSelectionTracker<T> {
    fun isSelected(item: T): Boolean
}