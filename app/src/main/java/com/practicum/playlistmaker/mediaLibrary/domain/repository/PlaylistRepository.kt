package com.practicum.playlistmaker.mediaLibrary.domain.repository

import android.net.Uri
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist, imageUri: Uri?): Long
    suspend fun updatePlaylist(playlist: Playlist, imageUri: Uri?): Boolean
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean
    suspend fun getPlaylists(): List<Playlist>
    suspend fun getPlaylistTracks(trackIds: List<Long>): List<Track>
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    suspend fun deletePlaylist(playlistId: Long)
}