package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var coverImage: ImageView
    private lateinit var titleText: TextView
    private lateinit var artistText: TextView
    private lateinit var playButton: ImageButton
    private lateinit var favoriteButton: ImageButton
    private lateinit var addToPlaylistButton: ImageButton
    private lateinit var durationPlaceholder: TextView

    private lateinit var albumRow: View
    private lateinit var yearRow: View
    private lateinit var genreRow: View
    private lateinit var countryRow: View
    private lateinit var durationRow: View

    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var trackDuration: TextView

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isPlaying = false

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { mp ->
                if (isPlaying) {
                    val position = mp.currentPosition / 1000
                    val minutes = position / 60
                    val seconds = position % 60
                    durationPlaceholder.text = String.format("%d:%02d", minutes, seconds)
                    handler.postDelayed(this, MEDIA_PLAYER_DELAY)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        // UI
        coverImage = findViewById(R.id.cover_image)
        titleText = findViewById(R.id.track_title)
        artistText = findViewById(R.id.track_artist)

        playButton = findViewById(R.id.play_button)
        favoriteButton = findViewById(R.id.favorite_button)
        addToPlaylistButton = findViewById(R.id.add_to_playlist_button)
        durationPlaceholder = findViewById(R.id.duration_placeholder)

        albumRow = findViewById(R.id.album_row)
        yearRow = findViewById(R.id.year_row)
        genreRow = findViewById(R.id.genre_row)
        countryRow = findViewById(R.id.country_row)
        durationRow = findViewById(R.id.duration_row)

        trackAlbum = findViewById(R.id.track_album)
        trackYear = findViewById(R.id.track_year)
        trackGenre = findViewById(R.id.track_genre)
        trackCountry = findViewById(R.id.track_country)
        trackDuration = findViewById(R.id.track_duration)

        val track = intent.getParcelableExtra<Track>(EXTRA_TRACK)
        track?.let { bind(it) }

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        playButton.setOnClickListener {
            togglePlayback()
        }
    }

    private fun bind(track: Track) {
        titleText.text = track.trackName
        artistText.text = track.artistName

        if (!track.collectionName.isNullOrEmpty()) {
            albumRow.visibility = View.VISIBLE
            trackAlbum.text = track.collectionName
        } else albumRow.visibility = View.GONE

        if (!track.releaseDate.isNullOrEmpty()) {
            yearRow.visibility = View.VISIBLE
            trackYear.text = track.releaseDate.take(4)
        } else yearRow.visibility = View.GONE

        if (!track.primaryGenreName.isNullOrEmpty()) {
            genreRow.visibility = View.VISIBLE
            trackGenre.text = track.primaryGenreName
        } else genreRow.visibility = View.GONE

        if (!track.country.isNullOrEmpty()) {
            countryRow.visibility = View.VISIBLE
            trackCountry.text = track.country
        } else countryRow.visibility = View.GONE

        durationRow.visibility = View.VISIBLE
        trackDuration.text = track.trackTime

        val artworkUrl = track.artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg")
        Glide.with(this).load(artworkUrl).apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder_34)).into(coverImage)

        if (!track.previewUrl.isNullOrEmpty()) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(track.previewUrl)
                prepareAsync()
                setOnPreparedListener {
                    playButton.isEnabled = true
                }
                setOnCompletionListener {
                    stopPlayback()
                }
            }
        } else {
            playButton.isEnabled = false
        }
    }

    private fun togglePlayback() {
        mediaPlayer?.let { mp ->
            if (isPlaying) {
                mp.pause()
                isPlaying = false
                handler.removeCallbacks(updateTimeRunnable)
                playButton.setImageResource(R.drawable.ic_play_image)
            } else {
                mp.start()
                isPlaying = true
                handler.post(updateTimeRunnable)
                playButton.setImageResource(R.drawable.ic_pause_image)
            }
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.seekTo(0)
        isPlaying = false
        handler.removeCallbacks(updateTimeRunnable)
        playButton.setImageResource(R.drawable.ic_play_image)
        durationPlaceholder.text = getString(R.string.duration_placeholder_null)
    }

    override fun onStop() {
        super.onStop()
        if (isPlaying) {
            stopPlayback()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
            handler.removeCallbacks(updateTimeRunnable)
            playButton.setImageResource(R.drawable.ic_play_image)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"
        private const val MEDIA_PLAYER_DELAY: Long = 500
    }
}

