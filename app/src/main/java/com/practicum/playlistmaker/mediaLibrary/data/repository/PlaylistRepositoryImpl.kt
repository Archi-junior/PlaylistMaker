package com.practicum.playlistmaker.mediaLibrary.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.PlaylistTrackEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.TrackEntity
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PlaylistRepositoryImpl(
    private val db: AppDatabase,
    private val context: Context
) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist, imageUri: Uri?): Long {
        val coverPath = imageUri?.let { uri ->
            saveCoverToInternalStorage(uri)
        }

        val entity = PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            coverPath = coverPath,
            tracksCount = 0
        )

        return db.playlistDao().insert(entity)
    }

    override suspend fun updatePlaylist(playlist: Playlist, imageUri: Uri?): Boolean {
        return try {
            val coverPath = if (imageUri != null) {
                saveCoverToInternalStorage(imageUri)
            } else {
                playlist.coverPath
            }

            val entity = PlaylistEntity(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                coverPath = coverPath,
                tracksCount = playlist.tracksCount
            )

            db.playlistDao().update(entity)

            if (imageUri != null && playlist.coverPath != null) {
                val oldFile = File(playlist.coverPath)
                if (oldFile.exists()) {
                    oldFile.delete()
                }
            }

            true
        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Error updating playlist", e)
            false
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return db.playlistDao().getAllPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val entity = db.playlistDao().getPlaylistById(id)
        if (entity != null) {
            val trackIds = db.playlistTrackDao().getTrackIdsByPlaylistId(id)
            val playlist = entity.toDomain().copy(trackIds = trackIds)
            return playlist
        }
        return null
    }

    override suspend fun getPlaylistTracks(trackIds: List<Long>): List<Track> {
        if (trackIds.isEmpty()) {
            return emptyList()
        }
        return db.trackDao().getTracksByIds(trackIds).map { entity ->
            entity.toDomain()
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        db.playlistDao().removeTrackFromPlaylist(playlistId, trackId)

        val usageCount = db.playlistDao().getTrackUsageCount(trackId)
        if (usageCount == 0) {
            db.playlistTrackDao().deleteByTrackId(trackId)
        }

        updatePlaylistTracksCount(playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        val playlist = db.playlistDao().getPlaylistById(playlistId) ?: return

        val trackIds = db.playlistDao().getPlaylistTrackIds(playlistId).map { it.trackId }

        db.playlistDao().delete(playlist)

        trackIds.forEach { trackId ->
            val usageCount = db.playlistDao().getTrackUsageCount(trackId)
            if (usageCount == 0) {
                db.playlistTrackDao().deleteByTrackId(trackId)
            }
        }

        playlist.coverPath?.let { coverPath ->
            File(coverPath).delete()
        }
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean {
        return try {
            db.trackDao().insert(track.toEntity())
            val playlistTrack = PlaylistTrackEntity(
                playlistId = playlistId,
                trackId = track.trackId
            )
            db.playlistTrackDao().insert(playlistTrack)
            val newCount = db.playlistTrackDao().getTracksCount(playlistId)
            db.playlistDao().updateTracksCount(playlistId, newCount)
            true
        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Error adding track to playlist", e)
            false
        }
    }

    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
        return db.playlistTrackDao().isTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return db.playlistDao().getAllPlaylists().first().map { it.toDomain() }
    }

    private suspend fun saveCoverToInternalStorage(uri: Uri): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return@withContext null

            val playlistCoversDir = File(context.filesDir, "playlist_covers")
            if (!playlistCoversDir.exists()) {
                playlistCoversDir.mkdirs()
            }

            val file = File(playlistCoversDir, "cover_${System.currentTimeMillis()}.jpg")

            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            Log.d("PlaylistRepository", "Cover saved to: ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Error saving cover", e)
            null
        }
    }

    private suspend fun updatePlaylistTracksCount(playlistId: Long) {
        val trackCount = db.playlistDao().getPlaylistTrackIds(playlistId).size
        val playlist = db.playlistDao().getPlaylistById(playlistId) ?: return

        val updatedPlaylist = playlist.copy(tracksCount = trackCount)
        db.playlistDao().update(updatedPlaylist)
    }

    private fun PlaylistEntity.toDomain(): Playlist {
        return Playlist(
            id = id,
            name = name,
            description = description,
            coverPath = coverPath,
            tracksCount = tracksCount
        )
    }

    private fun Track.toEntity(): TrackEntity {
        return TrackEntity(
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
            trackTimeMillis = trackTimeMillis
        )
    }

    private fun TrackEntity.toDomain(): Track {
        return Track(
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
            trackTimeMillis = trackTimeMillis
        )
    }
}