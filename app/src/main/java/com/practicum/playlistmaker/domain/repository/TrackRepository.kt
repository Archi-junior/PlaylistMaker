package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.Track

interface TrackRepository {
    suspend fun searchTracks(query: String): Result<List<Track>>
}