package com.practicum.playlistmaker.main.presentation

sealed class MainUiEvent {
    object OpenSearch : MainUiEvent()
    object OpenSettings : MainUiEvent()
    object OpenLibrary : MainUiEvent()
}