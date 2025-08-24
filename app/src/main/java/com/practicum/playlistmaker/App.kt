package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class App : Application() {

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_DARK_THEME = "dark_theme"
    }

    private lateinit var sharedPreferences: SharedPreferences
    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        darkTheme = sharedPreferences.getBoolean(KEY_DARK_THEME, false)
        applyTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        sharedPreferences.edit {
            putBoolean(KEY_DARK_THEME, darkTheme)
        }

        applyTheme(darkTheme)
    }

    private fun applyTheme(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}


