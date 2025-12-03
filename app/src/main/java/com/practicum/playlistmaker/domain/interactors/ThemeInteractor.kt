package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.domain.repository.ThemeRepository

class ThemeInteractor(private val repo: ThemeRepository) : IThemeInteractor {
    override fun isDark(): Boolean = repo.getTheme()
    override fun setDark(enabled: Boolean) = repo.saveTheme(enabled)
}