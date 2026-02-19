package com.practicum.playlistmaker.mediaLibrary.domain.repository

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist, coverFile: File?): Long
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long): Boolean
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean
    suspend fun getPlaylists(): List<Playlist>
}