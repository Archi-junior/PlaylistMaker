package com.practicum.playlistmaker.mediaLibrary.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaylistTrackDao {
    @Insert
    suspend fun insert(playlistTrack: PlaylistTrackEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId)")
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getTracksCount(playlistId: Long): Int

    @Query("SELECT * FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun getPlaylistsByTrackId(trackId: Long): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun deleteByTrackId(trackId: Long)

    @Query("SELECT trackId FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY rowid DESC")
    suspend fun getTrackIdsByPlaylistId(playlistId: Long): List<Long>
}