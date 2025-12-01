package com.practicum.playlistmaker.presentation.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.data.network.ItunesApiService
import com.practicum.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker.domain.interactors.HistoryInteractor
import com.practicum.playlistmaker.domain.interactors.SearchTracksInteractor
import com.practicum.playlistmaker.domain.interactors.ThemeInteractor
import com.practicum.playlistmaker.domain.repository.TrackRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {

    private var _historyRepository: SearchHistoryRepositoryImpl? = null
    private var _themeRepository: ThemeRepositoryImpl? = null
    private var _trackRepository: TrackRepository? = null

    private var _itunesApiService: ItunesApiService? = null

    fun init(appContext: Context) {
        val context = appContext.applicationContext
        val gson = Gson()

        _historyRepository = SearchHistoryRepositoryImpl(context, gson)
        _themeRepository = ThemeRepositoryImpl(context)

        _itunesApiService = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)

        _trackRepository = TrackRepositoryImpl(_itunesApiService!!)
    }

    val themeInteractor: ThemeInteractor
        get() = ThemeInteractor(_themeRepository!!)

    val historyInteractor: HistoryInteractor
        get() = HistoryInteractor(_historyRepository!!)

    val searchInteractor: SearchTracksInteractor
        get() = SearchTracksInteractor(_trackRepository!!)
}