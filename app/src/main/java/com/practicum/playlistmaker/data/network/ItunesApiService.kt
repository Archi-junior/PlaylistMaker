package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.network.model.ItunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("search?entity=song")
    suspend fun searchTracks(@Query("term") text: String): ItunesResponse
}