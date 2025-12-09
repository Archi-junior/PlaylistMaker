package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerViewModelFactory(
    private val interactor: PlayerInteractor,
    private val track: Track
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(interactor, track) as T
    }
}
