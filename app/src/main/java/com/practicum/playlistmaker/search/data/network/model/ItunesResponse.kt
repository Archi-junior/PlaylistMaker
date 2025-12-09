package com.practicum.playlistmaker.search.data.network.model

import com.practicum.playlistmaker.search.data.network.dto.ItunesTrackDto

data class ItunesResponse(
    val resultCount: Int,
    val results: List<ItunesTrackDto>
)