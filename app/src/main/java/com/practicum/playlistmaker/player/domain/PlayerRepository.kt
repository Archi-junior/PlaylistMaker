package com.practicum.playlistmaker.player.domain

interface PlayerRepository {
    suspend fun prepare(url: String, onPrepared: () -> Unit, onFinished: () -> Unit)
    fun play()
    fun pause()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun release()
}