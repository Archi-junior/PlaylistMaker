package com.practicum.playlistmaker.mediaLibrary.data.repository

import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.PlaylistTrackEntity
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
            val isTrackExists = db.playlistTrackDao().isTrackInPlaylist(playlistId, trackId)

            if (!isTrackExists) {
                val playlistTrack = PlaylistTrackEntity(
                    playlistId = playlistId,
                    trackId = trackId
                )
                db.playlistTrackDao().insert(playlistTrack)

                val newCount = db.playlistTrackDao().getTracksCount(playlistId)
                db.playlistDao().updateTracksCount(playlistId, newCount)

                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    override suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
        return db.playlistTrackDao().isTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return db.playlistDao().getAllPlaylists().first().map { it.toDomain() }
    }
}