package com.practicum.playlistmaker.search.domain.interactors

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface ISearchTracksInteractor {
    fun searchTracks(query: String): Flow<List<Track>>
}