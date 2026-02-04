package com.practicum.playlistmaker.app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.domainModule
import com.practicum.playlistmaker.di.networkModule
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
                viewModelModule
            )
        }
    }
    private fun applyTheme() {
        val prefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val isDarkTheme = prefs.getBoolean("dark_theme", false)

        val mode = if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}