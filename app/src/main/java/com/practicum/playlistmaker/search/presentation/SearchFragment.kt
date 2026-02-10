package com.practicum.playlistmaker.search.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.presentation.adapter.TrackAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val viewModel: SearchViewModel by viewModel()
    private var searchQuery: String = ""

    private var searchDebounceJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupListeners()
        observeViewModel()

        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
            binding.searchEditText.setText(searchQuery)
        }
        when {
            searchQuery.isNotEmpty() -> performSearchDebounced(searchQuery)
            else -> viewModel.loadHistory()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerViews() {
        adapter = TrackAdapter { track ->
            viewModel.onTrackClicked(track)
            binding.tracksRecyclerView.post {
                openPlayer(track)
            }
        }
        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRecyclerView.adapter = adapter

        historyAdapter = TrackAdapter { track ->
            viewModel.addToHistory(track)
            openPlayer(track)
        }
        binding.historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecycler.adapter = historyAdapter
    }

    private fun setupListeners() {
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
                if (searchQuery.isNotEmpty()) {
                    performSearchDebounced(searchQuery)
                } else {
                    showHistoryIfEmptyQuery()
                }
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding.searchEditText.addTextChangedListener { text ->
            val query = text?.toString()?.trim() ?: ""
            searchQuery = query
            binding.clearButton.isVisible = query.isNotEmpty()
            searchDebounceJob?.cancel()

            if (query.isNotEmpty()) {
                searchDebounceJob = lifecycleScope.launch {
                    delay(DEBOUNCE_DELAY_TIME)
                    if (query == binding.searchEditText.text.toString().trim()) {
                        viewModel.searchTracks(query)
                    }
                }
            } else {
                showHistoryIfEmptyQuery()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: SearchState) {
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
            is SearchState.Empty -> {
                binding.placeholderLayout.isVisible = true
            }
            is SearchState.Error -> {
                binding.placeholderLayout.isVisible = true
            }
            is SearchState.Idle -> {
                val history = viewModel.getHistorySync()
                if (history.isNotEmpty()) {
                    showHistory(history)
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

    private fun performSearchDebounced(query: String) {
        searchDebounceJob?.cancel()
        searchDebounceJob = lifecycleScope.launch {
            viewModel.searchTracks(query)
        }
    }

    private fun openPlayer(track: Track) {
        val bundle = Bundle().apply {
            putParcelable("track", track)
        }
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment, bundle)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        binding.searchEditText.clearFocus()
    }

    companion object {
        private const val DEBOUNCE_DELAY_TIME = 500L
        private const val SEARCH_QUERY_KEY = "SEARCH_QUERY_KEY"
        fun newInstance() = SearchFragment()
    }
}