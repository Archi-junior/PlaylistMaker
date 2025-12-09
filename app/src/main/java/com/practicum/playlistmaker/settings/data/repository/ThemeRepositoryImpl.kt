package com.practicum.playlistmaker.settings.data.repository

import android.content.Context
import com.practicum.playlistmaker.settings.domain.repository.ThemeRepository

class ThemeRepositoryImpl(context: Context) : ThemeRepository {

    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    private val KEY = "dark_theme"

    override fun getTheme(): Boolean {
        return prefs.getBoolean(KEY, false)
    }

    override fun saveTheme(enabled: Boolean) {
        prefs.edit().putBoolean(KEY, enabled).apply()
    }
}