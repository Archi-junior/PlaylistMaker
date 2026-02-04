package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.presentation.MainViewModel
import com.practicum.playlistmaker.medialibrary.presentation.FavoritesViewModel
import com.practicum.playlistmaker.medialibrary.presentation.PlaylistsViewModel
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(
            searchInteractor = get(),
            historyInteractor = get()
        )
    }

    viewModel {
        FavoritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }

    viewModel {
        SettingsViewModel(
            themeInteractor = get()
        )
    }

    viewModel { params ->
        PlayerViewModel(
            interactor = get(),
            track = params.get()
        )
    }

    viewModel {
        MainViewModel()
    }
}