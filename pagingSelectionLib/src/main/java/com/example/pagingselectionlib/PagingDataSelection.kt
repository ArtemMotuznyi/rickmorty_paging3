package com.example.pagingselectionlib

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

data class PagingDataSelection<T : Parcelable>(
    val state: State,
    var items: List<T> = mutableListOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()?.let(State::valueOf) ?: State.DESELECTED_ALL,
        mutableListOf<T>().apply{

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                parcel.readParcelableList(this, Parcelable::class.java.classLoader)
            }else{
                parcel.readList(this, Parcelable::class.java.classLoader)
            }
        }
    )

    fun isSelectedItem(item: T): Boolean = when (state) {
        State.DESELECTED_ALL -> false
        State.SELECTION -> items.contains(item)
        State.SELECTED_ALL -> true
        State.DESELECTION -> !items.contains(item)
    }

    enum class State {
        DESELECTED_ALL,
        SELECTION,
        SELECTED_ALL,
        DESELECTION,
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(state.name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeParcelableList(items, 0)
        }else{
            parcel.writeTypedList(items)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PagingDataSelection<Parcelable>> {
        override fun createFromParcel(parcel: Parcel): PagingDataSelection<Parcelable> {
            return PagingDataSelection(parcel)
        }

        override fun newArray(size: Int): Array<PagingDataSelection<Parcelable>?> {
            return arrayOfNulls(size)
        }
    }
}