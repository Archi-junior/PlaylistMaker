package com.practicum.playlistmaker.player.data.repository

import com.practicum.playlistmaker.player.data.db.FavoriteTracksDao
import com.practicum.playlistmaker.player.data.db.FavoriteTrackEntity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val dao: FavoriteTracksDao
) : FavoriteTracksRepository {

    override suspend fun addToFavorites(track: Track) {
        dao.insert(track.toEntity())
    }

    override suspend fun removeFromFavorites(track: Track) {
        dao.delete(track.toEntity())
    }

    override fun getAllFavorites(): Flow<List<Track>> {
        return dao.getAllFavorites().map { entities ->
            entities.map { it.toTrack() }
        }
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return dao.getFavoriteById(trackId) != null
    }

    override suspend fun getAllFavoriteIds(): List<Long> {
        return dao.getAllFavoriteIds()
    }

    private fun Track.toEntity(): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTime = trackTime,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl,
            artworkUrl512 = artworkUrl512,
            trackTimeMillis = trackTimeMillis
        )
    }

    private fun FavoriteTrackEntity.toTrack(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            trackTime = trackTime,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl,
            isFavorite = true
        )
    }
}