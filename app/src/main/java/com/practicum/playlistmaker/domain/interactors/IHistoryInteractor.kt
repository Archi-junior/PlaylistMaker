package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.domain.models.Track

interface IHistoryInteractor {
    fun addTrack(track: Track)
    fun clearHistory()
    fun getHistory(): List<Track>
}