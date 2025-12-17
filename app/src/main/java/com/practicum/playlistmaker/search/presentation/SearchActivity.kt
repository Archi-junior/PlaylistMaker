package com.practicum.playlistmaker.search.presentation

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.presentation.PlayerActivity
import com.practicum.playlistmaker.search.presentation.adapter.TrackAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val viewModel: SearchViewModel by viewModel()

    private var searchJob: Job? = null
    private var searchQuery: String = ""

    companion object {
        private const val SEARCH_TIMEOUT = 300L
        private const val SEARCH_QUERY_KEY = "SEARCH_QUERY_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        setupListeners()
        observeViewModel()
        viewModel.loadHistory()
    }

    override fun onResume() {
        super.onResume()
        if (binding.searchEditText.text.isEmpty()) {
            viewModel.loadHistory()
        }
    }

    private fun setupRecyclerViews() {
        adapter = TrackAdapter { track ->
            viewModel.addToHistoryWithoutEmit(track)
            binding.tracksRecyclerView.post {
                openPlayer(track)
            }
        }
        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.tracksRecyclerView.adapter = adapter

        historyAdapter = TrackAdapter { track ->
            viewModel.addToHistory(track)
            openPlayer(track)
        }
        binding.historyRecycler.layoutManager = LinearLayoutManager(this)
        binding.historyRecycler.adapter = historyAdapter
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { finish() }

        binding.clearButton.setOnClickListener {
            binding.searchEditText.text.clear()
            showHistoryIfEmptyQuery()
            hideKeyboard()
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            showHistoryIfEmptyQuery()
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchQuery = binding.searchEditText.text.toString().trim()
                if (searchQuery.isNotEmpty()) performSearchDebounced(searchQuery, true)
                else showHistoryIfEmptyQuery()
                hideKeyboard()
                true
            } else false
        }

        binding.searchEditText.addTextChangedListener { text ->
            val query = text?.toString()?.trim() ?: ""
            binding.clearButton.isVisible = query.isNotEmpty()
            searchRunnable(query)
        }
    }

    private fun searchRunnable(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(SEARCH_TIMEOUT)
            searchQuery = query
            if (query.isNotEmpty()) performSearchDebounced(query)
            else showHistoryIfEmptyQuery()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.progressBar.isVisible = false
                binding.placeholderLayout.isVisible = false
                binding.tracksRecyclerView.isVisible = false
                binding.historyLayout.isVisible = false
                binding.historyTitle.isVisible = false
                binding.historyRecycler.isVisible = false
                binding.clearHistoryButton.isVisible = false

                when (state) {
                    is SearchState.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is SearchState.Content -> {
                        binding.tracksRecyclerView.isVisible = true
                        adapter.submitList(state.tracks)
                    }
                    is SearchState.History -> {
                        showHistory(state.tracks)
                    }
                    is SearchState.Empty, is SearchState.Error -> {
                        binding.placeholderLayout.isVisible = true
                    }
                    is SearchState.Idle -> {
                        val history = viewModel.getHistorySync()
                        if (history.isNotEmpty()) showHistory(history)
                    }
                }
            }
        }
    }

    private fun showHistory(history: List<Track>) {
        if (history.isNotEmpty()) {
            binding.historyLayout.isVisible = true
            binding.historyTitle.isVisible = true
            binding.historyRecycler.isVisible = true
            binding.clearHistoryButton.isVisible = true
            historyAdapter.submitList(history)
        } else {
            binding.historyLayout.isVisible = false
            binding.historyTitle.isVisible = false
            binding.historyRecycler.isVisible = false
            binding.clearHistoryButton.isVisible = false
        }
    }

    private fun showHistoryIfEmptyQuery() {
        if (binding.searchEditText.text.isEmpty()) {
            val history = viewModel.getHistorySync()
            showHistory(history)
        }
    }

    private fun performSearchDebounced(query: String, force: Boolean = false) {
        viewModel.searchTracks(query, force)
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        binding.searchEditText.clearFocus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
        binding.searchEditText.setText(searchQuery)
        if (searchQuery.isNotEmpty()) performSearchDebounced(searchQuery, true)
        else showHistoryIfEmptyQuery()
    }
}