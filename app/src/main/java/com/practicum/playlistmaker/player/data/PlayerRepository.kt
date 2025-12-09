package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer

class PlayerRepository(private val mediaPlayer: MediaPlayer) {

    fun play() = mediaPlayer.start()
    fun pause() { mediaPlayer.pause() }
    fun position(): Int = mediaPlayer.currentPosition
    fun release() { mediaPlayer.release() }
    fun getCurrentPosition(): Int = mediaPlayer.currentPosition
    fun isPlaying(): Boolean = mediaPlayer.isPlaying

    suspend fun prepare(url: String, onPrepared: () -> Unit, onFinished: () -> Unit) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { onPrepared() }
        mediaPlayer.setOnCompletionListener { onFinished() }
    }
}
