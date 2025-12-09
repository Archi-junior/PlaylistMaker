package com.practicum.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.IThemeInteractor

sealed class SettingsEvent {
    object ShareApp : SettingsEvent()
    object ContactSupport : SettingsEvent()
    data class OpenUserAgreement(val url: String) : SettingsEvent()
}

class SettingsViewModel(
    private val themeInteractor: IThemeInteractor
) : ViewModel() {
    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    private val _events = MutableLiveData<SettingsEvent>()
    val events: LiveData<SettingsEvent> = _events

    init {
        _isDarkTheme.value = themeInteractor.isDark()
    }

    fun onThemeChanged(isDark: Boolean) {
        themeInteractor.saveTheme(isDark)
        _isDarkTheme.value = isDark
    }

    fun onShareAppClicked() {
        _events.value = SettingsEvent.ShareApp
    }

    fun onContactSupportClicked() {
        _events.value = SettingsEvent.ContactSupport
    }

    fun onUserAgreementClicked(url: String) {
        _events.value = SettingsEvent.OpenUserAgreement(url)
    }
}