package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.TrackRepository

class SearchTracksInteractor(private val repository: TrackRepository) {
    suspend fun searchTracks(query: String): List<Track> {
        return repository.searchTracks(query).getOrElse { emptyList() }
    }
}