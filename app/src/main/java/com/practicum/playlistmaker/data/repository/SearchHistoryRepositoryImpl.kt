package com.practicum.playlistmaker.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    context: Context,
    private val gson: Gson = Gson(),
    private val maxSize: Int = 10
) : SearchHistoryRepository {

    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val KEY = "tracks"

    override fun addTrack(track: Track) {
        val list = getHistory().toMutableList()
        // remove existing by id
        list.removeAll { it.trackId == track.trackId }
        list.add(0, track)
        if (list.size > maxSize) {
            while (list.size > maxSize) list.removeLast()
        }
        prefs.edit().putString(KEY, gson.toJson(list)).apply()
    }

    override fun getHistory(): List<Track> {
        val json = prefs.getString(KEY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun clear() {
        prefs.edit().remove(KEY).apply()
    }
}