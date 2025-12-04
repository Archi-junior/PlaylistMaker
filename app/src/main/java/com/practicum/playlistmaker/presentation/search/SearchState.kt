package com.practicum.playlistmaker.presentation.search

import com.practicum.playlistmaker.domain.models.Track

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
    object Empty : SearchState()
    object Error : SearchState()
}