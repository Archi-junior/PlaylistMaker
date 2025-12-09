package com.practicum.playlistmaker.player.domain

sealed class PlayerState {
    data object Idle : PlayerState()
    data object Prepared : PlayerState()
    data class Playing(val positionMs: Int) : PlayerState()
    data class Paused(val positionMs: Int) : PlayerState()
    data object Finished : PlayerState()
}