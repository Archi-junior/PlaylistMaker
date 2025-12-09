package com.practicum.playlistmaker.player.domain

class PlayerInteractor(private val repository: PlayerRepository) {

    suspend fun prepare(url: String, onPrepared: () -> Unit, onFinished: () -> Unit) {
        repository.prepare(url, onPrepared, onFinished)
    }

    fun play() = repository.play()
    fun pause() = repository.pause()
    fun getPositionMs() = repository.getCurrentPosition()
    fun isPlaying() = repository.isPlaying()
    fun release() = repository.release()
}