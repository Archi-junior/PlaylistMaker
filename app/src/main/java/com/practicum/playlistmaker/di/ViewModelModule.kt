package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.presentation.MainViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.FavoritesViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.NewPlaylistViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.PlaylistsViewModel
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.player.presentation.PlaylistSelectionViewModel
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
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
        PlaylistsViewModel(playlistInteractor = get())
    }

    viewModel {
        NewPlaylistViewModel(playlistInteractor = get(),  application = androidApplication())
    }

    viewModel {
        SettingsViewModel(
            themeInteractor = get()
        )
    }

    viewModel { params ->
        PlaylistSelectionViewModel(
            playlistInteractor = get(),
            track = params.get()
        )
    }

    viewModel { params ->
        PlayerViewModel(
            interactor = get(),
            favoritesInteractor = get(),
            track = params.get()
        )
    }

    viewModel {
        FavoritesViewModel(favoritesInteractor = get())
    }

    viewModel {
        MainViewModel()
    }
}