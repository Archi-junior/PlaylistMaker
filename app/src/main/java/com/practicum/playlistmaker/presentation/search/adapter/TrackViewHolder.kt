package com.practicum.playlistmaker.presentation.search.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.databinding.ItemTrackBinding
import com.practicum.playlistmaker.domain.models.Track

class TrackViewHolder(
    private val binding: ItemTrackBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Track) {
        binding.trackName.text = item.trackName
        binding.artistName.text = item.artistName

        Glide.with(binding.root)
            .load(item.artworkUrl100)
            .into(binding.trackArtwork)
    }
}