package com.practicum.playlistmaker.main.presentation

sealed interface MainUiEvent {
    object OpenSearch : MainUiEvent
    object OpenSettings : MainUiEvent
    object OpenLibrary : MainUiEvent
}