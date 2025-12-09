package com.practicum.playlistmaker.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.di.ServiceLocator
import com.practicum.playlistmaker.presentation.player.PlayerActivity
import com.practicum.playlistmaker.presentation.search.adapter.TrackAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val viewModel by lazy {
        SearchViewModel(
            searchInteractor = ServiceLocator.searchInteractor,
            historyInteractor = ServiceLocator.historyInteractor
        )
    }

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var searchQuery: String = ""
    private var isClickAllowed = true
    private val clickHandler = Handler(Looper.getMainLooper())
    private var searchJob: Job? = null

    companion object {
        private const val SEARCH_TIMEOUT = 300L
        private const val CLICK_DEBOUNCE = 1000L
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

    private fun setupRecyclerViews() {
        adapter = TrackAdapter { track ->
            if (clickDebounce()) {
                lifecycleScope.launch {
                    viewModel.addToHistoryWithoutEmit(track)
                }
                openPlayer(track)
            }
        }
        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.tracksRecyclerView.adapter = adapter

        historyAdapter = TrackAdapter { track ->
            if (clickDebounce()) {
                viewModel.addToHistory(track)
                openPlayer(track)
            }
        }
        binding.historyRecycler.layoutManager = LinearLayoutManager(this)
        binding.historyRecycler.adapter = historyAdapter
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { finish() }

        binding.clearButton.setOnClickListener {
            binding.searchEditText.text.clear()
            binding.clearButton.isVisible = false
            adapter.submitList(emptyList())
            showHistoryIfEmptyQuery()
            hideKeyboard()
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            showHistoryIfEmptyQuery()
        }

        binding.refreshButton.setOnClickListener {
            if (searchQuery.isNotEmpty()) performSearchDebounced(searchQuery, force = true)
        }

        binding.searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                binding.clearButton.isVisible = query.isNotEmpty()
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    searchQuery = query
                    if (query.isNotEmpty()) performSearchDebounced(query)
                    else showHistoryIfEmptyQuery()
                }
                searchHandler.postDelayed(searchRunnable!!, SEARCH_TIMEOUT)
            }
        })

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchQuery = binding.searchEditText.text.toString().trim()
                binding.clearButton.isVisible = searchQuery.isNotEmpty()
                if (searchQuery.isNotEmpty()) performSearchDebounced(searchQuery)
                else showHistoryIfEmptyQuery()
                hideKeyboard()
                true
            } else false
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.progressBar.visibility = View.GONE
                binding.placeholderLayout.visibility = View.GONE
                binding.tracksRecyclerView.visibility = View.GONE
                binding.historyLayout.visibility = View.GONE

                when (state) {
                    is SearchState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is SearchState.Content -> {
                        binding.tracksRecyclerView.visibility = View.VISIBLE
                        adapter.submitList(state.tracks)
                    }
                    is SearchState.History -> {
                        binding.historyLayout.visibility = View.VISIBLE
                        binding.historyTitle.visibility = View.VISIBLE
                        binding.historyRecycler.visibility = View.VISIBLE
                        binding.clearHistoryButton.visibility = View.VISIBLE
                        historyAdapter.submitList(state.tracks)
                    }
                    is SearchState.Empty, is SearchState.Error -> binding.placeholderLayout.visibility = View.VISIBLE
                    is SearchState.Idle -> {
                        val history = viewModel.getHistorySync()
                        if (history.isNotEmpty()) {
                            binding.historyLayout.visibility = View.VISIBLE
                            historyAdapter.submitList(history)
                        } else {
                            binding.historyLayout.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun showHistoryIfEmptyQuery() {
        if (binding.searchEditText.text.isEmpty()) {
            val history = viewModel.getHistorySync()
            if (history.isNotEmpty()) {
                binding.historyLayout.visibility = View.VISIBLE
                binding.historyTitle.visibility = View.VISIBLE
                binding.historyRecycler.visibility = View.VISIBLE
                binding.clearHistoryButton.visibility = View.VISIBLE
                historyAdapter.submitList(history)
            } else {
                binding.historyLayout.visibility = View.GONE
                binding.historyTitle.visibility = View.GONE
                binding.historyRecycler.visibility = View.GONE
                binding.clearHistoryButton.visibility = View.GONE
            }
        }
    }


    private fun performSearchDebounced(query: String, force: Boolean = false) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            if (!force) delay(SEARCH_TIMEOUT)
            viewModel.searchTracks(query)
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE)
        }
        return current
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
        if (searchQuery.isNotEmpty()) performSearchDebounced(searchQuery, force = true)
        else showHistoryIfEmptyQuery()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
    }
}