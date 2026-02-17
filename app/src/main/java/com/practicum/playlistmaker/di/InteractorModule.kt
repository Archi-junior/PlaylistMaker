package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.mediaLibrary.domain.interactors.IPlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.interactors.PlaylistInteractor
import org.koin.dsl.module

val interactorModule = module {
    single<IPlaylistInteractor> {
        PlaylistInteractor(repository = get())
    }
}