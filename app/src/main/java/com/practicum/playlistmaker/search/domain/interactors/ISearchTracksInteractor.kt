package com.practicum.playlistmaker.search.domain.interactors

import com.practicum.playlistmaker.search.domain.models.Track

interface ISearchTracksInteractor {
    suspend fun searchTracks(query: String): List<Track>
}