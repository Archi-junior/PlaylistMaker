package com.practicum.playlistmaker.search.domain.interactors

import com.practicum.playlistmaker.search.data.network.dto.ItunesTrackDto
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.toTrackTime
import com.practicum.playlistmaker.search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchTracksInteractor(private val repository: TrackRepository) : ISearchTracksInteractor {
    override fun searchTracks(query: String): Flow<List<Track>> = flow {
        repository.searchTracks(query).collect { result ->
            result.fold(onSuccess = { tracks -> emit(tracks) }, onFailure = { emit(emptyList()) })
        }
    }
}

fun ItunesTrackDto.toTrack(): Track {
    return Track(
        trackId = trackId ?: 0L,
        trackName = trackName.orEmpty(),
        artistName = artistName.orEmpty(),
        trackTimeMillis = trackTimeMillis ?: 0L,
        trackTime = (trackTimeMillis ?: 0L).toTrackTime(),
        artworkUrl100 = artworkUrl100.orEmpty(),
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}