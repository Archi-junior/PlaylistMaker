package com.practicum.playlistmaker.player.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(requireArguments().getParcelable<Track>("track")!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayerBinding.bind(view)

        bindTrack(viewModel.track)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.playButton.setOnClickListener {
            viewModel.onPlayClicked()
        }
        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { screenState ->
                when (screenState.playerState) {

                    PlayerState.Idle -> {
                        binding.playButton.isEnabled = false
                        binding.playButton.setImageResource(R.drawable.ic_play_image)
                        binding.durationPlaceholder.text =
                            getString(R.string.duration_placeholder_text)
                    }

                    PlayerState.Prepared -> {
                        binding.playButton.isEnabled = true
                        binding.playButton.setImageResource(R.drawable.ic_play_image)
                    }

                    is PlayerState.Playing -> {
                        binding.playButton.setImageResource(R.drawable.ic_pause_image)
                        binding.durationPlaceholder.text = formatTime(screenState.playerState.positionMs.toLong())
                    }

                    is PlayerState.Paused -> {
                        binding.playButton.setImageResource(R.drawable.ic_play_image)
                    }

                    PlayerState.Finished -> {
                        binding.playButton.setImageResource(R.drawable.ic_play_image)
                        binding.durationPlaceholder.text =
                            getString(R.string.duration_placeholder_null)
                    }
                }
                updateFavoriteButton(screenState.isFavorite)
            }
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val drawable = if (isFavorite) {
            R.drawable.ic_favorite_border_filled
        } else {
            R.drawable.ic_favorite_border
        }
        binding.favoriteButton.setImageResource(drawable)
    }

    private fun bindTrack(track: Track) {
        binding.trackTitle.text = track.trackName
        binding.trackArtist.text = track.artistName

        Glide.with(this)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.ic_image_placeholder_34)
            .into(binding.coverImage)
        binding.trackDuration.text = track.trackTime
        binding.durationRow.visibility = View.VISIBLE

        binding.albumRow.visibility =
            setRowText(binding.trackAlbum, track.collectionName)

        binding.yearRow.visibility =
            setRowText(binding.trackYear, track.releaseDate?.take(4))

        binding.genreRow.visibility =
            setRowText(binding.trackGenre, track.primaryGenreName)

        binding.countryRow.visibility =
            setRowText(binding.trackCountry, track.country)
    }

    private fun setRowText(view: android.widget.TextView, text: String?): Int {
        return if (!text.isNullOrEmpty()) {
            view.text = text
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return String.format("%02d:%02d", min, sec)
    }

    companion object {
        private const val ARG_TRACK = "track"

        fun newInstance(track: Track): PlayerFragment {
            return PlayerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TRACK, track)
                }
            }
        }
    }
}