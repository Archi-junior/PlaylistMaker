package com.practicum.playlistmaker.search.domain.interactors

import com.practicum.playlistmaker.search.domain.models.Track

interface IHistoryInteractor {
    fun addTrack(track: Track)
    fun clearHistory()
    fun getHistory(): List<Track>
}