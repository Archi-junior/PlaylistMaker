package com.practicum.playlistmaker.player.presentation

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.data.PlayerRepository
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { mp ->
                if (isPlaying) {
                    val position = mp.currentPosition / 1000
                    val minutes = position / 60
                    val seconds = position % 60
                    binding.durationPlaceholder.text = String.format("%d:%02d", minutes, seconds)
                    if (position < 30 && mp.isPlaying) { // остановка после 30 секунд
                        handler.postDelayed(this, 500)
                    } else {
                        stopPlayback()
                    }
                }
            }
        }
    }

    private val viewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory(
            PlayerInteractor(PlayerRepository(MediaPlayer())),
            intent.getParcelableExtra("track")!!
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = viewModel.track
        bindTrack(track)

        binding.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.playButton.setOnClickListener { togglePlayback() }

        observeViewModel()
    }

    private fun bindTrack(track: Track) {
        binding.trackTitle.text = track.trackName
        binding.trackArtist.text = track.artistName

        Glide.with(this)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.ic_image_placeholder_34)
            .into(binding.coverImage)

        binding.trackDuration.text = formatTime(track.trackTimeMillis)

        binding.albumRow.visibility = if (!track.collectionName.isNullOrEmpty()) {
            binding.trackAlbum.text = track.collectionName
            View.VISIBLE
        } else View.GONE

        binding.yearRow.visibility = if (!track.releaseDate.isNullOrEmpty()) {
            binding.trackYear.text = track.releaseDate.take(4)
            View.VISIBLE
        } else View.GONE

        binding.genreRow.visibility = if (!track.primaryGenreName.isNullOrEmpty()) {
            binding.trackGenre.text = track.primaryGenreName
            View.VISIBLE
        } else View.GONE

        binding.countryRow.visibility = if (!track.country.isNullOrEmpty()) {
            binding.trackCountry.text = track.country
            View.VISIBLE
        } else View.GONE

        // Настройка MediaPlayer
        if (!track.previewUrl.isNullOrEmpty()) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(track.previewUrl)
                prepareAsync()
                setOnPreparedListener {
                    binding.playButton.isEnabled = true
                }
                setOnCompletionListener {
                    stopPlayback()
                }
            }
        } else {
            binding.playButton.isEnabled = false
        }
    }

    private fun togglePlayback() {
        mediaPlayer?.let { mp ->
            if (isPlaying) {
                mp.pause()
                isPlaying = false
                handler.removeCallbacks(updateTimeRunnable)
                binding.playButton.setImageResource(R.drawable.ic_play_image)
            } else {
                mp.start()
                isPlaying = true
                handler.post(updateTimeRunnable)
                binding.playButton.setImageResource(R.drawable.ic_pause_image)
            }
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.seekTo(0)
        isPlaying = false
        handler.removeCallbacks(updateTimeRunnable)
        binding.playButton.setImageResource(R.drawable.ic_play_image)
        binding.durationPlaceholder.text = getString(R.string.duration_placeholder_text)
    }

    private fun formatTime(ms: Long): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return String.format("%02d:%02d", min, sec)
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
            handler.removeCallbacks(updateTimeRunnable)
            binding.playButton.setImageResource(R.drawable.ic_play_image)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                when (state) {
                    is PlayerState.Playing -> binding.trackDuration.text = formatTime(state.positionMs.toLong())
                    is PlayerState.Paused, PlayerState.Prepared, PlayerState.Idle, PlayerState.Finished -> {}
                }
            }
        }
    }
}