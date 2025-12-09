package com.practicum.playlistmaker.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerRepository
import com.practicum.playlistmaker.search.data.network.ItunesApiService
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.settings.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.interactors.HistoryInteractor
import com.practicum.playlistmaker.search.domain.interactors.IHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactors.ISearchTracksInteractor
import com.practicum.playlistmaker.settings.domain.IThemeInteractor
import com.practicum.playlistmaker.search.domain.interactors.SearchTracksInteractor
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import com.practicum.playlistmaker.search.domain.repository.TrackRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {

    private var _historyRepository: SearchHistoryRepositoryImpl? = null
    private var _themeRepository: ThemeRepositoryImpl? = null
    private var _trackRepository: TrackRepository? = null
    private var _itunesApiService: ItunesApiService? = null

    private var _playerRepository: PlayerRepository? = null
    private var _playerInteractor: PlayerInteractor? = null

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
        _playerRepository = PlayerRepositoryImpl()
        _playerInteractor = PlayerInteractor(_playerRepository!!)
    }

    val themeInteractor: IThemeInteractor
        get() = ThemeInteractor(_themeRepository!!)

    val historyInteractor: IHistoryInteractor
        get() = HistoryInteractor(_historyRepository!!)

    val searchInteractor: ISearchTracksInteractor
        get() = SearchTracksInteractor(_trackRepository!!)

    fun providePlayerRepository(): PlayerRepository = _playerRepository!!

    fun providePlayerInteractor(): PlayerInteractor = _playerInteractor!!
}