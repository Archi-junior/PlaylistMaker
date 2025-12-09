package com.practicum.playlistmaker.search.domain.interactors

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class HistoryInteractor(
    private val repository: SearchHistoryRepository
) : IHistoryInteractor {
    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun clearHistory() {
        repository.clear()
    }

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }
}