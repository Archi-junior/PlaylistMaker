package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.interactors.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.player.domain.interactors.IFavoritesInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val interactor: PlayerInteractor,
    private val favoritesInteractor: IFavoritesInteractor,
    val track: Track
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerScreenState())
    val state: StateFlow<PlayerScreenState> = _state.asStateFlow()
    private var tickerJob: Job? = null

    private fun createTimerFlow(): Flow<Int> = flow {
        while (interactor.isPlaying()) {
            emit(interactor.getPositionMs())
            delay(TIMER_DELAY)
        }
    }

    init {
        viewModelScope.launch {
            val isFavorite = favoritesInteractor.isFavorite(track.trackId)
            track.isFavorite = isFavorite
            _state.update { it.copy(isFavorite = isFavorite) }

            track.previewUrl?.let { url ->
                interactor.prepare(
                    url = url,
                    onPrepared = {
                        _state.update { it.copy(playerState = PlayerState.Prepared) }
                    },
                    onFinished = {
                        _state.update { it.copy(playerState = PlayerState.Finished) }
                        stopTimer()
                    }
                )
            }
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val newFavoriteState = favoritesInteractor.toggleFavorite(track)
            track.isFavorite = newFavoriteState
            _state.update { it.copy(isFavorite = newFavoriteState) }
        }
    }

    fun onPlayClicked() {
        when (_state.value.playerState) {
            is PlayerState.Playing -> pause()
            PlayerState.Prepared,
            PlayerState.Idle,
            is PlayerState.Paused,
            PlayerState.Finished -> start()
        }
    }

    private fun start() {
        interactor.play()
        startTimer()
    }

    private fun pause() {
        stopTimer()
        interactor.pause()
        _state.update {
            it.copy(playerState = PlayerState.Paused(interactor.getPositionMs()))
        }
    }

    private fun startTimer() {
        tickerJob?.cancel()
        tickerJob = createTimerFlow()
            .onEach { positionMs ->
                _state.update {
                    it.copy(playerState = PlayerState.Playing(positionMs))
                }
            }
            .launchIn(viewModelScope)
    }

    private fun stopTimer() {
        tickerJob?.cancel()
        tickerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        interactor.release()
    }

    companion object{
        const val TIMER_DELAY: Long = 300
    }
}