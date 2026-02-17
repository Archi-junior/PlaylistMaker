package com.practicum.playlistmaker.mediaLibrary.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE playlists SET tracksCount = tracksCount + 1, trackIds = :trackIds WHERE id = :playlistId")
    suspend fun addTrackToPlaylist(playlistId: Long, trackIds: String)

    @Transaction
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        val playlist = getPlaylistById(playlistId) ?: return
        val updatedTrackIds = playlist.trackIds.toMutableList().apply { add(trackId) }
        update(
            playlist.copy(
                tracksCount = playlist.tracksCount + 1,
                trackIds = updatedTrackIds
            )
        )
    }
}