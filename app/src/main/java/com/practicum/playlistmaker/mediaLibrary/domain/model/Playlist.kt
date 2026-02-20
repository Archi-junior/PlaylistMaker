package com.practicum.playlistmaker.mediaLibrary.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val coverPath: String? = null,
    val tracksCount: Int = 0,
    val trackIds: List<Long> = emptyList()
) : Parcelable