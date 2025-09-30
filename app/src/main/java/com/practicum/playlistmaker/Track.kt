package com.practicum.playlistmaker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val trackTimeMillis: Long,
    val previewUrl: String?
) : Parcelable

data class ItunesResponse(
    val resultCount: Int,
    val results: List<ItunesTrack>
)

data class ItunesTrack(
    val trackId: Long?,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) {
    fun toTrack(): Track {
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
}
