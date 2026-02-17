package com.practicum.playlistmaker.mediaLibrary.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LongListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        val type = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(value, type)
    }
}