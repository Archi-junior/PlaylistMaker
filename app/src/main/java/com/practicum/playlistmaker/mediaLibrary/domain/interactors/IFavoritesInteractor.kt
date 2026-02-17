package com.practicum.playlistmaker.mediaLibrary.domain.interactors

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.mediaLibrary.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow

interface IFavoritesInteractor {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    fun getAllFavorites(): Flow<List<Track>>
    suspend fun toggleFavorite(track: Track): Boolean
    suspend fun isFavorite(trackId: Long): Boolean
}

class FavoritesInteractor(
    private val repository: FavoriteTracksRepository
) : IFavoritesInteractor {

    override suspend fun addToFavorites(track: Track) {
        repository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: Track) {
        repository.removeFromFavorites(track)
    }

    override fun getAllFavorites(): Flow<List<Track>> {
        return repository.getAllFavorites()
    }

    override suspend fun toggleFavorite(track: Track): Boolean {
        return if (track.isFavorite) {
            removeFromFavorites(track)
            false
        } else {
            addToFavorites(track)
            true
        }
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return repository.isFavorite(trackId)
    }
}