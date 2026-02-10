package com.practicum.playlistmaker.settings.domain

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.repository.ThemeRepository

class ThemeInteractor(private val repo: ThemeRepository) : IThemeInteractor {
    override fun isDark(): Boolean = repo.getTheme()
    override fun setDark(enabled: Boolean) {
        val mode = if (enabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
    override fun saveTheme(isDark: Boolean) {
        repo.saveTheme(isDark)
    }
}