package com.practicum.playlistmaker.settings.domain

import com.practicum.playlistmaker.settings.domain.repository.ThemeRepository

class ThemeInteractor(private val repo: ThemeRepository) : IThemeInteractor {
    override fun isDark(): Boolean = repo.getTheme()
    override fun setDark(enabled: Boolean) = repo.saveTheme(enabled)
    override fun saveTheme(isDark: Boolean) {
        repo.saveTheme(isDark)
    }
}