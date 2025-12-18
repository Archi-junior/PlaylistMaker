    package com.practicum.playlistmaker.di

    import android.media.MediaPlayer
    import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
    import com.practicum.playlistmaker.player.domain.PlayerRepository
    import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
    import com.practicum.playlistmaker.search.data.repository.TrackRepositoryImpl
    import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
    import com.practicum.playlistmaker.search.domain.repository.TrackRepository
    import com.practicum.playlistmaker.settings.data.repository.ThemeRepositoryImpl
    import com.practicum.playlistmaker.settings.domain.repository.ThemeRepository
    import org.koin.android.ext.koin.androidContext
    import org.koin.dsl.module

    val dataModule = module {

        single<SearchHistoryRepository> {
            SearchHistoryRepositoryImpl(
                context = androidContext(),
                gson = get()
            )
        }

        single<TrackRepository> {
            TrackRepositoryImpl(get())
        }

        single<ThemeRepository> {
            ThemeRepositoryImpl(context = androidContext())
        }

        single<PlayerRepository> {
            PlayerRepositoryImpl()
        }
    }