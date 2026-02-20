package com.practicum.playlistmaker.mediaLibrary.domain.interactors

import android.net.Uri
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface IPlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist, imageUri: Uri?): Long
    suspend fun updatePlaylist(playlist: Playlist, imageUri: Uri?): Boolean
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun getPlaylistTracks(trackIds: List<Long>): List<Track>
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean
    suspend fun getPlaylists(): List<Playlist>
}

class PlaylistInteractor(
    private val repository: PlaylistRepository
) : IPlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist, imageUri: Uri?): Long {
        return repository.createPlaylist(playlist, imageUri)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return repository.getPlaylistById(id)
    }

    override suspend fun getPlaylistTracks(trackIds: List<Long>): List<Track> {
        return repository.getPlaylistTracks(trackIds)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        return repository.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        return repository.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist, imageUri: Uri?): Boolean {
        return repository.updatePlaylist(playlist, imageUri)
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean {
        return repository.addTrackToPlaylist(playlistId, track)
    }

    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
        return repository.isTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return repository.getPlaylists()
    }
}