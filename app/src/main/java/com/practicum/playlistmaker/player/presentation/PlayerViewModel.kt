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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val interactor: PlayerInteractor,
    private val favoritesInteractor: IFavoritesInteractor,
    val track: Track
) : ViewModel() {

    private val _state = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val state: StateFlow<PlayerState> = _state
    private var tickerJob: Job? = null
    private val _isFavorite = MutableStateFlow(track.isFavorite)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private fun createTimerFlow(): Flow<Int> = flow {
        while (interactor.isPlaying()) {
            emit(interactor.getPositionMs())
            delay(TIMER_DELAY)
        }
        if (!interactor.isPlaying() && _state.value is PlayerState.Finished) {
            emit(0)
        }
    }

    init {
        track.previewUrl?.let { url ->
            viewModelScope.launch {
                _isFavorite.value = favoritesInteractor.isFavorite(track.trackId)
                interactor.prepare(
                    url = url,
                    onPrepared = { _state.value = PlayerState.Prepared },
                    onFinished = {
                        _state.value = PlayerState.Finished
                        stopTimer()
                        _state.value = PlayerState.Playing(0)
                    }
                )
            }
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val newFavoriteState = favoritesInteractor.toggleFavorite(track)
            _isFavorite.value = newFavoriteState
            track.isFavorite = newFavoriteState
        }
    }

    fun onPlayClicked() {
        when (_state.value) {
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
        _state.value = PlayerState.Paused(interactor.getPositionMs())
    }

    private fun startTimer() {
        tickerJob?.cancel()
        tickerJob = createTimerFlow()
            .onEach { positionMs ->
                _state.value = PlayerState.Playing(positionMs)
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