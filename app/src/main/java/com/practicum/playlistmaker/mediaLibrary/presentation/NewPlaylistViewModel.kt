package com.practicum.playlistmaker.mediaLibrary.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.interactors.IPlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.launch

sealed interface NewPlaylistState {
    data object Idle : NewPlaylistState
    data class Success(val playlistName: String) : NewPlaylistState
    data object Error : NewPlaylistState
}

class NewPlaylistViewModel(
    private val playlistInteractor: IPlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<NewPlaylistState>(NewPlaylistState.Idle)
    val state: LiveData<NewPlaylistState> = _state

    fun createPlaylist(name: String, description: String?, imageUri: Uri?) {
        if (name.isBlank()) return

        viewModelScope.launch {
            try {
                val playlist = Playlist(
                    name = name,
                    description = description?.takeIf { it.isNotBlank() }
                )

                val id = playlistInteractor.createPlaylist(playlist, imageUri)
                Log.d("NewPlaylistViewModel", "Playlist created with ID: $id")
                _state.value = NewPlaylistState.Success(name)
            } catch (e: Exception) {
                Log.e("NewPlaylistViewModel", "Error creating playlist", e)
                _state.value = NewPlaylistState.Error
            }
        }
    }
}