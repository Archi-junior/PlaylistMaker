package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlayerRepositoryImpl() : PlayerRepository {
    private var mediaPlayer = MediaPlayer()
    override fun play() = mediaPlayer.start()
    override fun pause() = mediaPlayer.pause()
    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition
    override fun isPlaying(): Boolean = mediaPlayer.isPlaying
    override fun release() = mediaPlayer.release()

    override suspend fun prepare(url: String, onPrepared: () -> Unit, onFinished: () -> Unit) {
        withContext(Dispatchers.Main) {
            mediaPlayer.release()
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener { onPrepared() }
            mediaPlayer.setOnCompletionListener { onFinished() }
            mediaPlayer.prepareAsync()
        }
    }
}
