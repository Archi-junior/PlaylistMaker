package com.practicum.playlistmaker.data.mapper

import com.practicum.playlistmaker.data.network.dto.ItunesTrackDto
import com.practicum.playlistmaker.domain.models.Track

fun ItunesTrackDto.toDomain(): Track {
    val timeMillis = trackTimeMillis ?: 0L
    val minutes = timeMillis / 1000 / 60
    val seconds = timeMillis / 1000 % 60
    val formatted = String.format("%d:%02d", minutes, seconds)

    return Track(
        trackId = trackId ?: 0L,
        trackName = trackName ?: "Unknown",
        artistName = artistName ?: "Unknown",
        trackTime = formatted,
        artworkUrl100 = artworkUrl100 ?: "",
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        trackTimeMillis = timeMillis,
        previewUrl = previewUrl
    )
}