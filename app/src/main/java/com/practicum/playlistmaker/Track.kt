package com.practicum.playlistmaker

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String
)
data class ItunesResponse(
    val resultCount: Int,
    val results: List<ItunesTrack>
)

data class ItunesTrack(
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?
) {
    fun toTrack(): Track {
        val timeMillis = trackTimeMillis ?: 0L
        val minutes = timeMillis / 1000 / 60
        val seconds = timeMillis / 1000 % 60
        val formatted = String.format("%d:%02d", minutes, seconds)

        return Track(
            trackName = trackName ?: "Unknown",
            artistName = artistName ?: "Unknown",
            trackTime = formatted,
            artworkUrl100 = artworkUrl100 ?: ""
        )
    }
}
