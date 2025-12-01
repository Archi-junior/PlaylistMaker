package com.practicum.playlistmaker.presentation.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.models.Track

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isPlaying = false

    private var track: Track? = null

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { mp ->
                if (isPlaying) {
                    val position = mp.currentPosition / 1000
                    val minutes = position / 60
                    val seconds = position % 60
                    binding.durationPlaceholder.text = String.format("%d:%02d", minutes, seconds)
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = intent.getParcelableExtra("track")
        track?.let { bindTrack(it) }

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.playButton.setOnClickListener {
            togglePlayback()
        }
    }

    private fun bindTrack(track: Track) {
        binding.trackTitle.text = track.trackName
        binding.trackArtist.text = track.artistName

        try {
            val customFont = resources.getFont(R.font.ys_medium_400)
            binding.trackTitle.typeface = customFont
            binding.trackArtist.typeface = customFont
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Glide.with(this)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_image_placeholder_34)
            .into(binding.coverImage)

        binding.trackDuration.text = track.trackTime

        binding.albumRow.visibility = if (!track.collectionName.isNullOrEmpty()) {
            binding.trackAlbum.text = track.collectionName
            android.view.View.VISIBLE
        } else android.view.View.GONE

        binding.yearRow.visibility = if (!track.releaseDate.isNullOrEmpty()) {
            binding.trackYear.text = track.releaseDate.take(4)
            android.view.View.VISIBLE
        } else android.view.View.GONE

        binding.genreRow.visibility = if (!track.primaryGenreName.isNullOrEmpty()) {
            binding.trackGenre.text = track.primaryGenreName
            android.view.View.VISIBLE
        } else android.view.View.GONE

        binding.countryRow.visibility = if (!track.country.isNullOrEmpty()) {
            binding.trackCountry.text = track.country
            android.view.View.VISIBLE
        } else android.view.View.GONE

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
}