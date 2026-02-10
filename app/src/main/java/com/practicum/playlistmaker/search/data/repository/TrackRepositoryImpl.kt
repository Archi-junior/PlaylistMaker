package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.mapper.toDomain
import com.practicum.playlistmaker.search.data.network.ItunesApiService
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.TrackRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TrackRepositoryImpl(
    private val api: ItunesApiService
) : TrackRepository {

    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        val response = api.searchTracks(query)
        val mapped = response.results.map { it.toDomain() }
        emit(Result.success(mapped))
    }.catch { e ->
        when (e) {
            is CancellationException -> throw e
            else -> emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}