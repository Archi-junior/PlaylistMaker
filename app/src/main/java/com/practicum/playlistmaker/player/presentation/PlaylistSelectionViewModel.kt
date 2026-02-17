package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.interactors.IPlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class PlaylistSelectionViewModel(
    private val playlistInteractor: IPlaylistInteractor,
    private val track: Track
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            val playlists = playlistInteractor.getPlaylists()
            _playlists.postValue(playlists)
        }
    }

    fun onPlaylistSelected(playlist: Playlist) {
        viewModelScope.launch {
            val isAdded = playlistInteractor.addTrackToPlaylist(playlist.id, track.trackId)

            val message = if (isAdded) {
                "Добавлено в плейлист ${playlist.name}"
            } else {
                "Трек уже добавлен в плейлист ${playlist.name}"
            }

            _toastMessage.postValue(message)
        }
    }
}