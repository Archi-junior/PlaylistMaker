package com.practicum.playlistmaker.search.data.local.mapper

import com.practicum.playlistmaker.search.data.local.dto.TrackHistoryDto
import com.practicum.playlistmaker.search.domain.models.Track

fun Track.toHistoryDto() = TrackHistoryDto(
    trackId,
    trackName,
    artistName,
    trackTime,
    artworkUrl100,
    collectionName,
    releaseDate,
    primaryGenreName,
    country,
    trackTimeMillis,
    previewUrl
)

fun TrackHistoryDto.toDomain() = Track(
    trackId,
    trackName,
    artistName,
    trackTime,
    artworkUrl100,
    collectionName,
    releaseDate,
    primaryGenreName,
    country,
    trackTimeMillis,
    previewUrl
)