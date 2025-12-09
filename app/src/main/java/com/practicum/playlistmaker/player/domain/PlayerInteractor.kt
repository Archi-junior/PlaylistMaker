package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.data.PlayerRepository
import kotlinx.coroutines.delay

class PlayerInteractor(
    private val repository: PlayerRepository
) {

    suspend fun prepare(
        url: String,
        onPrepared: () -> Unit,
        onFinished: () -> Unit
    ) {
        repository.prepare(url, onPrepared, onFinished)
    }

    fun play() = repository.play()

    fun pause() = repository.pause()

    fun getPositionMs(): Int = repository.getCurrentPosition()

    fun isPlaying(): Boolean = repository.isPlaying()

    fun release() = repository.release()

    suspend fun positionTicker(onTick: (Int) -> Unit) {
        while (repository.isPlaying()) {
            delay(300L)
            onTick(repository.getCurrentPosition())
        }
    }
}