package com.practicum.playlistmaker.main.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.utils.Event

class MainViewModel : ViewModel() {

    private val _events = MutableLiveData<Event<MainUiEvent>>()
    val events: LiveData<Event<MainUiEvent>> = _events

    fun onSearchClicked() {
        _events.value = Event(MainUiEvent.OpenSearch)
    }

    fun onLibraryClicked() {
        _events.value = Event(MainUiEvent.OpenLibrary)
    }

    fun onSettingsClicked() {
        _events.value = Event(MainUiEvent.OpenSettings)
    }
}