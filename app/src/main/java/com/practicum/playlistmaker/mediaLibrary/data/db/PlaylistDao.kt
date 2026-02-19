package com.practicum.playlistmaker.mediaLibrary.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert
    suspend fun insert(playlist: PlaylistEntity): Long

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): PlaylistEntity?

    @Update
    suspend fun update(playlist: PlaylistEntity)

    @Delete
    suspend fun delete(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getPlaylistTrackIds(playlistId: Long): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun getTrackUsageCount(trackId: Long): Int

    @Query("UPDATE playlists SET tracksCount = :count WHERE id = :playlistId")
    suspend fun updateTracksCount(playlistId: Long, count: Int)
}