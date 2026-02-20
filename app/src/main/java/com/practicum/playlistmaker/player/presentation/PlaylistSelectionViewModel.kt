package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
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

    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    private val _toastMessageArg = MutableLiveData<String>()

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
            val isAdded = playlistInteractor.addTrackToPlaylist(playlist.id, track)
            if (isAdded) {
                _toastMessage.postValue(R.string.track_added_to_playlist)
                _toastMessageArg.postValue(playlist.name)
            } else {
                _toastMessage.postValue(R.string.track_already_in_playlist)
                _toastMessageArg.postValue(playlist.name)
            }
        }
    }
}