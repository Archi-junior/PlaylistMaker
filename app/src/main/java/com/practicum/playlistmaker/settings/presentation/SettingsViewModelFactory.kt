package com.practicum.playlistmaker.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.settings.domain.IThemeInteractor

class SettingsViewModelFactory(
    private val themeInteractor: IThemeInteractor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(themeInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}