package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository

class HistoryInteractor(private val repository: SearchHistoryRepository) {
    fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    fun clearHistory() {
        repository.clear()
    }

    fun getHistory(): List<Track> {
        return repository.getHistory()
    }
}