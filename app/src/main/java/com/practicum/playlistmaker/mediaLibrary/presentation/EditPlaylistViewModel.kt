package com.practicum.playlistmaker.mediaLibrary.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.interactors.IPlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: IPlaylistInteractor,
    private val playlist: Playlist?
) : ViewModel() {

    private val _state = MutableLiveData<NewPlaylistState>(NewPlaylistState.Idle)
    val state: LiveData<NewPlaylistState> = _state

    private val _playlistData = MutableLiveData<Playlist>()
    val playlistData: LiveData<Playlist> = _playlistData

    private var currentPlaylistId: Long = playlist?.id ?: 0

    init {
        playlist?.let {
            _playlistData.value = it
            currentPlaylistId = it.id
        }
    }

    fun updatePlaylist(name: String, description: String?, imageUri: Uri?) {
        if (name.isBlank() || playlist == null) return

        viewModelScope.launch {
            try {
                val updatedPlaylist = playlist.copy(
                    name = name,
                    description = description?.takeIf { it.isNotBlank() }
                )

                val success = playlistInteractor.updatePlaylist(updatedPlaylist, imageUri)

                if (success) {
                    currentPlaylistId = playlist.id
                    _state.value = NewPlaylistState.Success(name)
                } else {
                    _state.value = NewPlaylistState.Error
                }
            } catch (e: Exception) {
                _state.value = NewPlaylistState.Error
            }
        }
    }

    fun getPlaylistId(): Long = currentPlaylistId
}