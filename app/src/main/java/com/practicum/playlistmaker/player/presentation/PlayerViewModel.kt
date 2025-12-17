package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val interactor: PlayerInteractor,
    val track: Track
) : ViewModel() {

    private val _state = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val state: StateFlow<PlayerState> = _state
    private var tickerJob: Job? = null

    init {
        track.previewUrl?.let { url ->
            viewModelScope.launch {
                interactor.prepare(
                    url = url,
                    onPrepared = { _state.value = PlayerState.Prepared },
                    onFinished = {
                        _state.value = PlayerState.Finished
                        stopTimer()
                    }
                )
            }
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
        tickerJob = viewModelScope.launch {
            while (interactor.isPlaying()) {
                delay(TIMER_DELAY)
                _state.value = PlayerState.Playing(interactor.getPositionMs())
            }
        }
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