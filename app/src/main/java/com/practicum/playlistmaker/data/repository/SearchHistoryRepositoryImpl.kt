package com.practicum.playlistmaker.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.local.dto.TrackHistoryDto
import com.practicum.playlistmaker.data.local.mapper.toDomain
import com.practicum.playlistmaker.data.local.mapper.toHistoryDto
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    context: Context,
    private val gson: Gson = Gson(),
    private val maxSize: Int = 10
) : SearchHistoryRepository {

    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)

    override fun addTrack(track: Track) {
        val dtoList = getHistoryDto().toMutableList()
        val dto = track.toHistoryDto()
        dtoList.removeAll { it.trackId == dto.trackId }
        dtoList.add(0, dto)

        while (dtoList.size > maxSize) {
            dtoList.removeLast()
        }

        prefs.edit().putString(KEY, gson.toJson(dtoList)).apply()
    }

    override fun getHistory(): List<Track> =
        getHistoryDto().map { it.toDomain() }

    private fun getHistoryDto(): List<TrackHistoryDto> {
        val json = prefs.getString(KEY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<TrackHistoryDto>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun clear() {
        prefs.edit().remove(KEY).apply()
    }

    companion object {
        private const val KEY = "tracks"
    }
}