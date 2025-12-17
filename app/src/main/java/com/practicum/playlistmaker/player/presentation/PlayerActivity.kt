package com.practicum.playlistmaker.player.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(intent.getParcelableExtra<Track>("track")!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = viewModel.track
        bindTrack(track)

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.playButton.setOnClickListener {
            viewModel.onPlayClicked()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                when (state) {

                    PlayerState.Idle -> {
                        binding.playButton.isEnabled = false
                        binding.playButton.setImageResource(R.drawable.ic_play_image)
                        binding.durationPlaceholder.text = getString(R.string.duration_placeholder_text)
                    }

                    PlayerState.Prepared -> {
                        binding.playButton.isEnabled = true
                        binding.playButton.setImageResource(R.drawable.ic_play_image)
                    }

                    is PlayerState.Playing -> {
                        binding.playButton.setImageResource(R.drawable.ic_pause_image)
                        binding.durationPlaceholder.text = formatTime(state.positionMs.toLong())
                    }

                    is PlayerState.Paused -> {
                        binding.playButton.setImageResource(R.drawable.ic_play_image)
                    }

                    PlayerState.Finished -> {
                        binding.playButton.setImageResource(R.drawable.ic_play_image)
                        binding.durationPlaceholder.text = getString(R.string.duration_placeholder_text)
                    }
                }
            }
        }
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
}