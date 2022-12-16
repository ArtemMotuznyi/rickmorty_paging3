package com.example.pagingselectionlib

import android.os.Parcelable
import androidx.paging.PagingDataAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

class PagingDataSelectionManager<T : Parcelable>(
    private val adapter: PagingDataAdapter<T, *>,
    selection: PagingDataSelection<T>? = null
) : PagingDataSelectionTracker<T> {

    private val _selection = MutableStateFlow(
        selection ?: PagingDataSelection<T>(PagingDataSelection.State.DESELECTED_ALL)
    )
    val selection = _selection.asStateFlow()

    private val adapterItems: List<T>
        get() = adapter.snapshot().items

    private val adapterItemsSize: Int
        get() = adapter.snapshot().items.size

    override fun isSelected(item: T) = selection.value.isSelectedItem(item)

    fun deselectAll() {
        _selection.update { PagingDataSelection(PagingDataSelection.State.DESELECTED_ALL) }
        adapter.notifyItemRangeChanged(0, adapterItemsSize, SelectedItemChangedPayload.UNSELECTED)
    }

    fun selectAll() {
        _selection.update { PagingDataSelection(PagingDataSelection.State.SELECTED_ALL) }
        adapter.notifyItemRangeChanged(0, adapterItemsSize, SelectedItemChangedPayload.SELECTED)
    }

    fun updateSelectionItem(item: T) {
        val newState = _selection.updateAndGet { generateNewState(it, item) }
        updateSelectionState(newState, item)
    }

    private fun generateNewState(selection: PagingDataSelection<T>, newItem: T): PagingDataSelection<T> {
        val currentItems = selection.items
        val currentState = selection.state

        val newItems = if (currentItems.contains(newItem)) {
            currentItems - newItem
        } else {
            currentItems + newItem
        }

        if (isDeselectionAll(currentState, newItems)) {
            return PagingDataSelection(PagingDataSelection.State.DESELECTED_ALL)
        }

        if (isSelectionAll(currentState, newItems)) {
            return PagingDataSelection(PagingDataSelection.State.SELECTED_ALL)
        }

        return when (currentState) {
            PagingDataSelection.State.DESELECTED_ALL -> {
                PagingDataSelection(PagingDataSelection.State.SELECTION, newItems)
            }
            PagingDataSelection.State.SELECTED_ALL -> {
                PagingDataSelection(PagingDataSelection.State.DESELECTION, newItems)
            }
            else -> {
                selection.copy(items = newItems)
            }
        }
    }

    private fun isDeselectionAll(
        selectionState: PagingDataSelection.State,
        newItems: List<T>
    ) = (selectionState == PagingDataSelection.State.SELECTION && newItems.isEmpty()) ||
        (selectionState == PagingDataSelection.State.DESELECTION && newItems.size == adapterItemsSize)

    private fun isSelectionAll(
        selectionState: PagingDataSelection.State,
        newItems: List<T>
    ) = (selectionState == PagingDataSelection.State.SELECTION && newItems.size == adapterItemsSize) ||
        (selectionState == PagingDataSelection.State.DESELECTION && newItems.isEmpty())

    private fun updateSelectionState(selection: PagingDataSelection<T>, newItem: T) {
        val index = adapterItems.indexOf(newItem)
        val (payload: SelectedItemChangedPayload, updatedCount: Int) = when (selection.state) {
            PagingDataSelection.State.DESELECTED_ALL -> {
                SelectedItemChangedPayload.UNSELECTED to adapterItemsSize
            }
            PagingDataSelection.State.SELECTED_ALL -> {
                SelectedItemChangedPayload.SELECTED to adapterItemsSize
            }
            else -> {
                val payload = if (selection.isSelectedItem(newItem)) {
                    SelectedItemChangedPayload.SELECTED
                } else {
                    SelectedItemChangedPayload.UNSELECTED
                }
                payload to 1
            }
        }

        adapter.notifyItemRangeChanged(index, updatedCount, payload)
    }

    enum class SelectedItemChangedPayload {
        SELECTED, UNSELECTED
    }
}