package com.practicum.playlistmaker.domain.interactors

interface IThemeInteractor {
    fun isDark(): Boolean
    fun setDark(enabled: Boolean)
}