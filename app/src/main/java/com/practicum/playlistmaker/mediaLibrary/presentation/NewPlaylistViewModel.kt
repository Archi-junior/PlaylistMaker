package com.practicum.playlistmaker.mediaLibrary.presentation

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.interactors.IPlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

sealed class NewPlaylistState {
    data object Idle : NewPlaylistState()
    data class Success(val playlistName: String) : NewPlaylistState()
    data object Error : NewPlaylistState()
}

class NewPlaylistViewModel(
    private val playlistInteractor: IPlaylistInteractor,
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableLiveData<NewPlaylistState>(NewPlaylistState.Idle)
    val state: LiveData<NewPlaylistState> = _state

    fun createPlaylist(name: String, description: String?, imageUri: Uri?) {
        if (name.isBlank()) return

        viewModelScope.launch {
            try {
                val coverFile = imageUri?.let { uri ->
                    uriToFile(uri)
                }

                val playlist = Playlist(
                    name = name,
                    description = description?.takeIf { it.isNotBlank() }
                )

                playlistInteractor.createPlaylist(playlist, coverFile)
                _state.value = NewPlaylistState.Success(name)
            } catch (e: Exception) {
                _state.value = NewPlaylistState.Error
            }
        }
    }

    private suspend fun uriToFile(uri: Uri): File? = withContext(Dispatchers.IO) {
        return@withContext try {
            val contentResolver: ContentResolver = application.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return@withContext null

            val playlistCoversDir = File(application.filesDir, "playlist_covers")
            if (!playlistCoversDir.exists()) {
                playlistCoversDir.mkdirs()
            }

            val file = File(playlistCoversDir, "cover_${System.currentTimeMillis()}.jpg")

            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}