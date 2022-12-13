package com.example.rickmorty_paging3.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.example.rickmorty_paging3.databinding.CharacterLayoutBinding
import com.example.rickmorty_paging3.model.RickMorty

/* PagingDataAdapter
    - to bind PagingData to a RecyclerView
 */
/* ViewHolder
    - describes an item view and metadata about its place within RecylerView
 */

class CharacterAdapter(private val listener: CharacterItemListener ): PagingDataAdapter<RickMorty, ImageViewHolder>(differCallback) {

    interface CharacterItemListener {
        fun onCLickCharacter(CharacterId: Int)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            CharacterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            listener
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currChar = getItem(position)

        if (currChar != null) {
            holder.bind(currChar)
        }
    }
}

class ImageViewHolder( val binding: CharacterLayoutBinding, private val listener: CharacterAdapter.CharacterItemListener ): ViewHolder(binding.root), View.OnClickListener {

    private lateinit var character: RickMorty

    init {
        binding.root.setOnClickListener(this)
    }

    fun bind(item: RickMorty) {
        this.character = item
        binding.apply {
            itemView.apply {
                tvDescription.text = "${item?.name}"
                speciesAndStatus.text = """${item?.species} - ${item?.status}"""

                val imageLink = item?.image
                imageView.load(imageLink) {
                    crossfade(true)
                    crossfade(1000)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        listener.onCLickCharacter(character.id)
    }
}