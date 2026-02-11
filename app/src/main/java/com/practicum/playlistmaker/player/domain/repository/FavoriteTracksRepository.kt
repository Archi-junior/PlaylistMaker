package com.practicum.playlistmaker.player.domain.repository

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    fun getAllFavorites(): Flow<List<Track>>
    suspend fun isFavorite(trackId: Long): Boolean
    suspend fun getAllFavoriteIds(): List<Long>
}