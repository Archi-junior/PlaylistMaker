package com.practicum.playlistmaker.search.domain.interactors

import com.practicum.playlistmaker.player.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class HistoryInteractor(
    private val repository: SearchHistoryRepository,
    private val favoritesRepository: FavoriteTracksRepository
) : IHistoryInteractor {
    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun clearHistory() {
        repository.clear()
    }

    override suspend fun getHistory(): List<Track> {
        val history = repository.getHistory()
        val favoriteIds = favoritesRepository.getAllFavoriteIds()
        return history.map { track ->
            track.copy(isFavorite = track.trackId in favoriteIds)
        }
    }
}