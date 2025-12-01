package com.practicum.playlistmaker.data.network.model

import com.practicum.playlistmaker.domain.models.ItunesTrack

data class ItunesResponse(
    val resultCount: Int,
    val results: List<ItunesTrack>
)
