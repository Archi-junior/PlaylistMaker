package com.practicum.playlistmaker.search.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.databinding.ItemTrackBinding
import com.practicum.playlistmaker.search.domain.models.Track

class TrackViewHolder(
    private val binding: ItemTrackBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = track.trackTime

        Glide.with(binding.root)
            .load(track.artworkUrl100)
            .into(binding.trackArtwork)
    }
}