package com.practicum.playlistmaker.domain.repository

interface ThemeRepository {
    fun getTheme(): Boolean
    fun saveTheme(enabled: Boolean)
}