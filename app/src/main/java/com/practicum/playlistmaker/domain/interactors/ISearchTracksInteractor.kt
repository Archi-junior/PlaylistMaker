package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.domain.models.Track

interface ISearchTracksInteractor {
    suspend fun searchTracks(query: String): List<Track>
}