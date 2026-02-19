package com.practicum.playlistmaker.mediaLibrary.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.interactors.IPlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.launch

sealed class PlaylistsState {
    object Empty : PlaylistsState()
    data class Content(val playlists: List<Playlist>) : PlaylistsState()
}

class PlaylistsViewModel(
    private val playlistInteractor: IPlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistsState>()
    val state: LiveData<PlaylistsState> = _state

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                _state.postValue(
                    if (playlists.isEmpty()) PlaylistsState.Empty
                    else PlaylistsState.Content(playlists)
                )
            }
        }
    }
}
