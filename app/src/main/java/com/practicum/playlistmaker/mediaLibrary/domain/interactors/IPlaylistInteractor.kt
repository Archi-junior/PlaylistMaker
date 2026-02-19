package com.practicum.playlistmaker.mediaLibrary.domain.interactors

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import java.io.File

interface IPlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist, coverFile: File?): Long
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long): Boolean
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean
    suspend fun getPlaylists(): List<Playlist>
}

class PlaylistInteractor(
    private val repository: PlaylistRepository
) : IPlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist, coverFile: File?): Long {
        return repository.createPlaylist(playlist, coverFile)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long): Boolean {
        return repository.addTrackToPlaylist(playlistId, trackId)
    }

    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
        return repository.isTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return repository.getPlaylists()
    }
}