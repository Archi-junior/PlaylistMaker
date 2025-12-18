package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlayerRepositoryImpl() : PlayerRepository {

    private var mediaPlayer: MediaPlayer? = null

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override suspend fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onFinished: () -> Unit
    ) {
        withContext(Dispatchers.Main) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(url)
                    setOnPreparedListener { onPrepared() }
                    setOnCompletionListener { onFinished() }
                    prepareAsync()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}