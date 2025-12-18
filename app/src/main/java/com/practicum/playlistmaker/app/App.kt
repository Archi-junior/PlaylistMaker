package com.practicum.playlistmaker.app

import android.app.Application
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.domainModule
import com.practicum.playlistmaker.di.networkModule
import com.practicum.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
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
}