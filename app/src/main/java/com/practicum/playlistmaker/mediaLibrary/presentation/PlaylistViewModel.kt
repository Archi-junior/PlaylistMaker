package com.practicum.playlistmaker.mediaLibrary.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.interactors.IPlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PlaylistScreenState {
    data object Loading : PlaylistScreenState()
    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
        val totalDuration: String
    ) : PlaylistScreenState()
    data object Error : PlaylistScreenState()
}

class PlaylistViewModel(
    private val playlistInteractor: IPlaylistInteractor,
    playlistId: Long
) : ViewModel() {
    private val _state = MutableStateFlow<PlaylistScreenState>(PlaylistScreenState.Loading)
    val state: StateFlow<PlaylistScreenState> = _state.asStateFlow()

    private var currentPlaylist: Playlist? = null
    private var currentTracks: List<Track> = emptyList()

    private val _shareEvent = MutableLiveData<String?>()
    val shareEvent: LiveData<String?> = _shareEvent

    private val _emptyPlaylistEvent = MutableLiveData<Boolean>()
    val emptyPlaylistEvent: LiveData<Boolean> = _emptyPlaylistEvent

    private val _navigateBackEvent = MutableLiveData<Boolean>()
    val navigateBackEvent: LiveData<Boolean> = _navigateBackEvent

    init {
        loadPlaylistData(playlistId)
    }

    private fun loadPlaylistData(playlistId: Long) {
        viewModelScope.launch {
            try {
                val playlist = playlistInteractor.getPlaylistById(playlistId)
                if (playlist == null) {
                    _state.value = PlaylistScreenState.Error
                    return@launch
                }

                currentPlaylist = playlist
                val tracks = playlistInteractor.getPlaylistTracks(playlist.trackIds)
                currentTracks = tracks
                if (tracks.isEmpty()) {
                    Log.d("PlaylistViewModel", "No tracks in playlist")
                } else {
                    tracks.forEachIndexed { index, track ->
                        Log.d("PlaylistViewModel", "Track $index: ${track.trackName}")
                    }
                }
                val totalDuration = calculateTotalDuration(tracks)
                _state.value = PlaylistScreenState.Content(
                    playlist = playlist,
                    tracks = tracks,
                    totalDuration = totalDuration
                )
            } catch (e: Exception) {
                _state.value = PlaylistScreenState.Error
            }
        }
    }

    fun onDeleteTrackClicked(track: Track) {
        viewModelScope.launch {
            val playlist = currentPlaylist ?: return@launch
            playlistInteractor.removeTrackFromPlaylist(playlist.id, track.trackId)
            loadPlaylistData(playlist.id)
        }
    }

    fun onDeletePlaylistClicked() {
        viewModelScope.launch {
            val playlist = currentPlaylist ?: return@launch
            playlistInteractor.deletePlaylist(playlist.id)
            _navigateBackEvent.value = true
        }
    }

    private fun calculateTotalDuration(tracks: List<Track>): String {
        val totalMillis = tracks.sumOf { it.trackTimeMillis }
        val minutes = totalMillis / 1000 / 60
        return String.format("%d %s", minutes, "минут")
    }

    fun onShareClicked() {
        if (currentTracks.isEmpty()) {
            _emptyPlaylistEvent.value = true
        } else {
            val shareText = buildShareText()
            _shareEvent.value = shareText
        }
    }

    private fun buildShareText(): String? {
        val playlist = currentPlaylist ?: return null
        val tracks = currentTracks

        return buildString {
            appendLine(playlist.name)
            playlist.description?.let { appendLine(it) }
            appendLine("${tracks.size} ${getTrackWord(tracks.size)}")
            appendLine()
            tracks.forEachIndexed { index, track ->
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})")
            }
        }
    }

    fun onShareEventHandled() {
        _shareEvent.value = null
    }

    fun onEmptyPlaylistEventHandled() {
        _emptyPlaylistEvent.value = false
    }

    fun onNavigateBackEventHandled() {
        _navigateBackEvent.value = false
    }

    fun refreshPlaylistData() {
        currentPlaylist?.id?.let { playlistId ->
            loadPlaylistData(playlistId)
        }
    }

    private fun getTrackWord(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "трек"
            count % 10 in 2..4 && (count % 100 !in 12..14) -> "трека"
            else -> "треков"
        }
    }
}