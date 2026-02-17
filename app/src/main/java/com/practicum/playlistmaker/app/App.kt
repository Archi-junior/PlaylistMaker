package com.practicum.playlistmaker.app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.domainModule
import com.practicum.playlistmaker.di.networkModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        applyTheme()
        startKoin {
            androidContext(this@App)
            modules(
                networkModule,
                dataModule,
                domainModule,
                viewModelModule,
                repositoryModule
            )
        }
    }

    private fun applyTheme() {
        val prefs = getSharedPreferences(THEME_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val isDarkTheme = prefs.getBoolean(DARK_THEME_KEY, false)

        val mode = if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    companion object {
        const val THEME_PREFERENCES_NAME = "theme_prefs"
        const val DARK_THEME_KEY = "dark_theme"
    }
}