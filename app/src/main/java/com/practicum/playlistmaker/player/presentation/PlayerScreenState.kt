package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.player.domain.PlayerState

data class PlayerScreenState(
    val playerState: PlayerState = PlayerState.Idle,
    val isFavorite: Boolean = false
)