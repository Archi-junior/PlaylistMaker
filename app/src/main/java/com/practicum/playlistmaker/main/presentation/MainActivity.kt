package com.practicum.playlistmaker.main.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.medialibrary.presentation.MediaLibraryActivity
import com.practicum.playlistmaker.search.presentation.SearchActivity
import com.practicum.playlistmaker.settings.presentation.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindListeners()
        observeViewModel()
    }

    private fun bindListeners() {
        binding.btnSearch.setOnClickListener {
            viewModel.onSearchClicked()
        }
        binding.btnLibrary.setOnClickListener {
            viewModel.onLibraryClicked()
        }
        binding.btnSettings.setOnClickListener {
            viewModel.onSettingsClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.events.observe(this) { event ->
            event?.getContentIfNotHandled()?.let { action ->
                when (action) {
                    MainUiEvent.OpenSearch ->
                        startActivity(Intent(this, SearchActivity::class.java))

                    MainUiEvent.OpenLibrary ->
                        startActivity(Intent(this, MediaLibraryActivity::class.java))

                    MainUiEvent.OpenSettings ->
                        startActivity(Intent(this, SettingsActivity::class.java))
                }
            }
        }
    }
}