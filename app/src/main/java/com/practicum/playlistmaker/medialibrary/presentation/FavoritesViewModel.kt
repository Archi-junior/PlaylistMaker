package com.practicum.playlistmaker.medialibrary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.interactors.IFavoritesInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FavoritesState {
    object Empty : FavoritesState()
    data class Content(val tracks: List<Track>) : FavoritesState()
}

class FavoritesViewModel(
    private val favoritesInteractor: IFavoritesInteractor
) : ViewModel() {

    private val _state = MutableStateFlow<FavoritesState>(FavoritesState.Empty)
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getAllFavorites().collect { tracks ->
                _state.value = if (tracks.isEmpty()) {
                    FavoritesState.Empty
                } else {
                    FavoritesState.Content(tracks)
                }
            }
        }
    }
}
