package com.practicum.playlistmaker.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.interactors.IHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactors.ISearchTracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: ISearchTracksInteractor,
    private val historyInteractor: IHistoryInteractor
) : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state

    private var searchJob: Job? = null
    fun loadHistory() {
        val history = historyInteractor.getHistory()
        _state.update {
            if (history.isEmpty()) SearchState.Idle
            else SearchState.History(history)
        }
    }

    fun getHistorySync(): List<Track> = historyInteractor.getHistory()

    fun addToHistory(track: Track) {
        historyInteractor.addTrack(track)
        loadHistory()
    }

    fun addToHistoryWithoutEmit(track: Track) {
        historyInteractor.addTrack(track)
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        _state.value = SearchState.Idle
    }

    fun searchTracks(query: String, force: Boolean = false) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (!force) delay(SEARCH_DEBOUNCE_DELAY)

            if (query.isBlank()) {
                loadHistory()
                return@launch
            }

            _state.value = SearchState.Loading

            try {
                val tracks = searchInteractor.searchTracks(query)
                _state.value = if (tracks.isEmpty()) SearchState.Empty else SearchState.Content(tracks)
            } catch (e: Exception) {
                _state.value = SearchState.Error
            }
        }
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}