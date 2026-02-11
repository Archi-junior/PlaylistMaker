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

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchInteractor: ISearchTracksInteractor,
    private val historyInteractor: IHistoryInteractor
) : ViewModel() {

    private var clickJob: Job? = null
    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(DEBOUNCE_DELAY_TIME)
                .collect { query ->
                    if (query.isNotEmpty()) {
                        performSearch(query)
                    } else {
                        loadHistory()
                    }
                }
        }
    }

    private var searchJob: Job? = null
    fun loadHistory() {
        viewModelScope.launch {
            val history = historyInteractor.getHistory()
            _state.update {
                if (history.isEmpty()) SearchState.Idle
                else SearchState.History(history)
            }
        }
    }

    suspend fun getHistorySync(): List<Track> = historyInteractor.getHistory()

    fun addToHistory(track: Track) {
        historyInteractor.addTrack(track)
        loadHistory()
    }

    fun addToHistoryWithoutEmit(track: Track) {
        viewModelScope.launch {
            historyInteractor.addTrack(track)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyInteractor.clearHistory()
            _state.value = SearchState.Idle
        }
    }

    fun performSearchImmediately(query: String) {
        if (query.isNotEmpty()) {
            performSearch(query)
        } else {
            loadHistory()
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchInteractor.searchTracks(query)
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

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun onTrackClicked(track: Track) {
        clickJob?.cancel()
        clickJob = viewModelScope.launch {
            delay(TRACK_CLICKED_DELAY_TIME)
            addToHistory(track)
        }
    }

    companion object {
        private const val DEBOUNCE_DELAY_TIME = 500L
        const val TRACK_CLICKED_DELAY_TIME = 300L
    }
}