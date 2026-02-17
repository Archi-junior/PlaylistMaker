package com.practicum.playlistmaker.mediaLibrary.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.practicum.playlistmaker.mediaLibrary.data.db.converters.LongListConverter

@Entity(tableName = "playlists")
@TypeConverters(LongListConverter::class)
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val tracksCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)