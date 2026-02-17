package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.repository.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.player.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.repository.TrackRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackRepository> {
        TrackRepositoryImpl(api = get())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(dao = get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(context = get())
    }
}