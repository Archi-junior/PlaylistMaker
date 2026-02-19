package com.practicum.playlistmaker.mediaLibrary.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistMenuBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import java.io.File

class PlaylistMenuBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentPlaylistMenuBinding? = null
    private val binding get() = _binding!!
    private var onShareClick: (() -> Unit)? = null
    private var onEditClick: (() -> Unit)? = null
    private var onDeleteClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlist = arguments?.getParcelable<Playlist>(ARG_PLAYLIST) ?: return

        bindPlaylistInfo(playlist)
        setupListeners()
    }

    private fun bindPlaylistInfo(playlist: Playlist) {
        binding.playlistName.text = playlist.name
        binding.tracksCount.text = resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount,
            playlist.tracksCount
        )

        if (playlist.coverPath != null) {
            Glide.with(this)
                .load(File(playlist.coverPath))
                .transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_corner_radius))
                )
                .placeholder(R.drawable.ic_image_placeholder_34)
                .into(binding.coverImage)
        } else {
            binding.coverImage.setImageResource(R.drawable.ic_image_placeholder_34)
        }
    }

    private fun setupListeners() {
        binding.shareMenuItem.setOnClickListener {
            onShareClick?.invoke()
            dismiss()
        }

        binding.editMenuItem.setOnClickListener {
            onEditClick?.invoke()
            dismiss()
        }

        binding.deleteMenuItem.setOnClickListener {
            onDeleteClick?.invoke()
            dismiss()
        }
    }

    fun setOnShareClickListener(listener: () -> Unit) {
        onShareClick = listener
    }

    fun setOnEditClickListener(listener: () -> Unit) {
        onEditClick = listener
    }

    fun setOnDeleteClickListener(listener: () -> Unit) {
        onDeleteClick = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PLAYLIST = "playlist"

        fun newInstance(playlist: Playlist): PlaylistMenuBottomSheet {
            return PlaylistMenuBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PLAYLIST, playlist)
                }
            }
        }
    }
}