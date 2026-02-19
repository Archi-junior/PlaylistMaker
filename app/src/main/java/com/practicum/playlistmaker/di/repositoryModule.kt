package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.mediaLibrary.data.repository.PlaylistRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.repository.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.mediaLibrary.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.repository.TrackRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackRepository> {
        TrackRepositoryImpl(api = get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            db = get(), context = get()
        )
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(dao = get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(context = get())
    }
}