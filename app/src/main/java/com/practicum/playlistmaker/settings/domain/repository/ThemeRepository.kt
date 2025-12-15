package com.practicum.playlistmaker.settings.domain.repository

interface ThemeRepository {
    fun getTheme(): Boolean
    fun saveTheme(enabled: Boolean)
}