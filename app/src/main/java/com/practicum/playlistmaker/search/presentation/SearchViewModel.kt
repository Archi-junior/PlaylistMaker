package com.practicum.playlistmaker.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.interactors.IHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactors.ISearchTracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: ISearchTracksInteractor,
    private val historyInteractor: IHistoryInteractor
) : ViewModel() {

    private var clickJob: Job? = null
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

    @OptIn(FlowPreview::class)
    fun searchTracks(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchInteractor.searchTracks(query)
                .debounce(SEARCH_DEBOUNCE_DELAY)
                .onStart { _state.value = SearchState.Loading }
                .catch {
                    _state.value = SearchState.Error
                }
                .collect { tracks ->
                    _state.value = if (tracks.isEmpty()) {
                        SearchState.Empty
                    } else {
                        SearchState.Content(tracks)
                    }
                }
        }
    }

    fun onTrackClicked(track: Track) {
        clickJob?.cancel()
        clickJob = viewModelScope.launch {
            delay(300)
            addToHistory(track)
        }
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}