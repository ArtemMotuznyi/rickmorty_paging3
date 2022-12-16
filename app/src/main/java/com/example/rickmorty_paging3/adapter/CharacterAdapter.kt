package com.example.rickmorty_paging3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.example.pagingselectionlib.PagingDataSelectionManager
import com.example.pagingselectionlib.PagingDataSelectionTracker
import com.example.rickmorty_paging3.databinding.CharacterLayoutBinding
import com.example.rickmorty_paging3.model.RickMorty

/* PagingDataAdapter
    - to bind PagingData to a RecyclerView
 */
/* ViewHolder
    - describes an item view and metadata about its place within RecylerView
 */

class CharacterAdapter(
    private val listener: CharacterItemListener
) : PagingDataAdapter<RickMorty, ImageViewHolder>(differCallback) {

    interface CharacterItemListener {
        fun onCLickCharacter(character: RickMorty)
        fun onLongClickListener(character: RickMorty)
    }

    companion object {
        val differCallback = object : DiffUtil.ItemCallback<RickMorty>() {
            override fun areItemsTheSame(oldItem: RickMorty, newItem: RickMorty): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RickMorty, newItem: RickMorty): Boolean {
                return oldItem == newItem
            }

        }
    }

    var tracker: PagingDataSelectionTracker<RickMorty>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            CharacterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currChar = getItem(position)

        if (currChar != null) {
            holder.bind(currChar, tracker?.isSelected(currChar) ?: false)
        }
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int, payloads: List<Any>) {
        val payload = payloads.firstOrNull()

        if (payload is PagingDataSelectionManager.SelectedItemChangedPayload) {
            holder.setSelected(payload === PagingDataSelectionManager.SelectedItemChangedPayload.SELECTED)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}

class ImageViewHolder(
    val binding: CharacterLayoutBinding,
    private val listener: CharacterAdapter.CharacterItemListener,
) : ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

    private var character: RickMorty? = null

    init {
        binding.root.setOnClickListener(this)
        binding.root.setOnLongClickListener(this)
    }

    fun bind(item: RickMorty, isSelected: Boolean) {
        this.character = item
        binding.apply {
            itemView.apply {
                tvDescription.text = "${item.name}"
                speciesAndStatus.text = """${item?.species} - ${item?.status}"""

                val imageLink = item.image
                imageView.load(imageLink) {
                    crossfade(true)
                    crossfade(1000)
                }
            }
        }

        setSelected(isSelected)
    }

    fun setSelected(selected: Boolean) {
        binding.overlay.isVisible = selected
    }

    override fun onClick(v: View?) {
        character?.let(listener::onCLickCharacter)
    }

    override fun onLongClick(p0: View?): Boolean {
        character?.let(listener::onLongClickListener)
        return true
    }
}