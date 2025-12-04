package com.practicum.playlistmaker.data.network.model

import com.practicum.playlistmaker.data.network.dto.ItunesTrackDto

data class ItunesResponse(
    val resultCount: Int,
    val results: List<ItunesTrackDto>
)