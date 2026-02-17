package com.practicum.playlistmaker.mediaLibrary.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import java.io.File

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private var playlists = listOf<Playlist>()

    fun submitList(newList: List<Playlist>) {
        playlists = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount() = playlists.size

    class PlaylistViewHolder(
        private val binding: ItemPlaylistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.tracksCount.text = itemView.context.resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.tracksCount,
                playlist.tracksCount
            )

            if (!playlist.coverPath.isNullOrBlank() && File(playlist.coverPath).exists()) {
                Glide.with(itemView)
                    .load(File(playlist.coverPath))
                    .transform(
                        CenterCrop(),
                        RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.playlist_cover_radius))
                    )
                    .placeholder(R.drawable.ic_image_placeholder_34)
                    .error(R.drawable.ic_image_placeholder_34)
                    .into(binding.coverImage)
            } else {
                binding.coverImage.setImageResource(R.drawable.ic_image_placeholder_34)
            }
        }
    }
}