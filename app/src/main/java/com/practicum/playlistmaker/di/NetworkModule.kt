package com.practicum.playlistmaker.di

import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.network.ItunesApiService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {

    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ItunesApiService> {
        get<Retrofit>().create(ItunesApiService::class.java)
    }

    single { Gson() }
}