package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.interactors.FavoritesInteractor
import com.practicum.playlistmaker.player.domain.interactors.IFavoritesInteractor
import com.practicum.playlistmaker.player.domain.interactors.PlayerInteractor
import com.practicum.playlistmaker.search.domain.interactors.HistoryInteractor
import com.practicum.playlistmaker.search.domain.interactors.IHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactors.ISearchTracksInteractor
import com.practicum.playlistmaker.search.domain.interactors.SearchTracksInteractor
import com.practicum.playlistmaker.settings.domain.IThemeInteractor
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import org.koin.dsl.module

val domainModule = module {
    single<IHistoryInteractor> {
        HistoryInteractor(
            repository = get(),
            favoritesRepository = get()
        )
    }

    single<ISearchTracksInteractor> {
        SearchTracksInteractor(
            repository = get(),
            favoritesRepository = get()
        )
    }

    single<IFavoritesInteractor> {
        FavoritesInteractor(repository = get())
    }

    single<IThemeInteractor> {
        ThemeInteractor(repo = get())
    }

    single {
        PlayerInteractor(repository = get())
    }
}