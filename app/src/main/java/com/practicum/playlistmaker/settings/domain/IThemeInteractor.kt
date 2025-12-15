package com.practicum.playlistmaker.settings.domain

interface IThemeInteractor {
    fun isDark(): Boolean
    fun setDark(enabled: Boolean)
    fun saveTheme(isDark: Boolean)
}