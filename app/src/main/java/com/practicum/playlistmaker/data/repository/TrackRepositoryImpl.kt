package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.mapper.toDomain
import com.practicum.playlistmaker.data.network.ItunesApiService
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepositoryImpl(
    private val api: ItunesApiService
) : TrackRepository {

    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.searchTracks(query)
                val mapped = response.results.map { it.toDomain() }
                Result.success(mapped)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}