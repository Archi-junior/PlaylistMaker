package com.practicum.playlistmaker.mediaLibrary.data.repository

import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream

class PlaylistRepositoryImpl(
    private val db: AppDatabase,
    private val filesDir: File
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist, coverFile: File?): Long {
        val coverPath = coverFile?.let { saveCoverToInternalStorage(it) }

        val entity = PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            coverPath = coverPath,
            tracksCount = 0
        )

        return db.playlistDao().insert(entity)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return db.playlistDao().getAllPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return db.playlistDao().getPlaylistById(id)?.toDomain()
    }

    private fun saveCoverToInternalStorage(sourceFile: File): String {
        val destinationFile = File(filesDir, "playlist_covers/${System.currentTimeMillis()}.jpg")
        destinationFile.parentFile?.mkdirs()

        FileOutputStream(destinationFile).use { output ->
            sourceFile.inputStream().use { input ->
                input.copyTo(output)
            }
        }

        return destinationFile.absolutePath
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

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long): Boolean {
        return try {
            val playlist = db.playlistDao().getPlaylistById(playlistId) ?: return false

            if (playlist.trackIds.contains(trackId)) {
                return false
            }

            val updatedTrackIds = playlist.trackIds.toMutableList().apply { add(trackId) }
            val updatedPlaylist = playlist.copy(
                tracksCount = playlist.tracksCount + 1,
                trackIds = updatedTrackIds
            )

            db.playlistDao().update(updatedPlaylist)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
        val playlist = db.playlistDao().getPlaylistById(playlistId) ?: return false
        return playlist.trackIds.contains(trackId)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return db.playlistDao().getAllPlaylists().first().map { it.toDomain() }
    }
}