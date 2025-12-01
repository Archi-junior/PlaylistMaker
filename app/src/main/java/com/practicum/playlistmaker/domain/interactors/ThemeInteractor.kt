package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.domain.repository.ThemeRepository

class ThemeInteractor(private val repo: ThemeRepository) {
    fun isDark(): Boolean = repo.getTheme()
    fun setDark(enabled: Boolean) = repo.saveTheme(enabled)
}