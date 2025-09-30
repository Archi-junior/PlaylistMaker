package com.practicum.playlistmaker

import ItunesApiService
import SearchHistoryManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter

    private lateinit var historyTitle: TextView
    private lateinit var historyRecycler: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var clearHistoryButton: Button
    private lateinit var historyManager: SearchHistoryManager

    private lateinit var apiService: ItunesApiService
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var placeholderTextSecond: TextView
    private lateinit var refreshButton: Button

    private lateinit var historyLayout: LinearLayout

    private var searchQuery: String = ""

    private val searchDebounceHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val clickDebounceHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var isClickAllowed = true

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        progressBar = findViewById(R.id.progress_bar)

        historyLayout = findViewById(R.id.history_layout)

        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        backButton = findViewById(R.id.back_button)
        recyclerView = findViewById(R.id.tracks_recycler_view)

        historyTitle = findViewById(R.id.history_title)
        historyRecycler = findViewById(R.id.history_recycler)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        historyManager = SearchHistoryManager(this)

        placeholderLayout = findViewById(R.id.placeholder_layout)
        placeholderImage = findViewById(R.id.placeholder_image)
        placeholderText = findViewById(R.id.placeholder_text)
        placeholderTextSecond = findViewById(R.id.placeholder_text_second)
        refreshButton = findViewById(R.id.refresh_button)

        setupRetrofit()
        setupRecyclerView()
        setupHistoryRecycler()
        setupSearchField()

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            clearButton.isVisible = false
            adapter.updateList(emptyList())
            showHistoryIfEmptyQuery()
            hideKeyboard()
        }

        backButton.setOnClickListener { finish() }

        refreshButton.setOnClickListener {
            if (searchQuery.isNotEmpty()) searchTracksOnline(searchQuery)
        }

        clearHistoryButton.setOnClickListener {
            historyManager.clear()
            updateHistory(show = true)
        }

        updateHistory(show = true)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickDebounceHandler.postDelayed({ isClickAllowed = true }, 1000) // 1 сек
        }
        return current
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder().baseUrl("https://itunes.apple.com/").addConverterFactory(GsonConverterFactory.create()).build()
        apiService = retrofit.create(ItunesApiService::class.java)
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(emptyList()) { track ->
            if (clickDebounce()) {
                historyManager.addTrack(track)
                updateHistory()
                openAudioPlayer(track)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupHistoryRecycler() {
        historyAdapter = TrackAdapter(emptyList()) { track ->
            if (clickDebounce()) {
                historyManager.addTrack(track)
                searchEditText.setText(track.trackName)
                searchEditText.setSelection(track.trackName.length)
                searchQuery = track.trackName
                searchTracksOnline(searchQuery)
                openAudioPlayer(track)
            }
        }
        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter
    }

    private fun setupSearchField() {
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchRunnable?.let { searchDebounceHandler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    searchQuery = s?.toString()?.trim() ?: ""
                    if (searchQuery.isNotEmpty()) {
                        searchTracksOnline(searchQuery)
                    } else {
                        adapter.updateList(emptyList())
                        showHistoryIfEmptyQuery()
                    }
                }
                searchDebounceHandler.postDelayed(searchRunnable!!, SEARCH_TIMEOUT) // 2 sec
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })


        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchQuery = searchEditText.text.toString().trim()
                clearButton.isVisible = searchQuery.isNotEmpty()

                if (searchQuery.isNotEmpty()) {
                    searchTracksOnline(searchQuery)
                } else {
                    adapter.updateList(emptyList())
                    showHistoryIfEmptyQuery()
                }
                hideKeyboard()
                true
            } else false
        }
    }

    private fun searchTracksOnline(query: String) {
        progressBar.visibility = View.VISIBLE
        apiService.searchTracks(query).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(call: Call<ItunesResponse>, response: Response<ItunesResponse>) {
                if (response.isSuccessful) {
                    progressBar.visibility = View.GONE
                    val tracks = response.body()?.results?.map { it.toTrack() } ?: emptyList()
                    if (tracks.isEmpty()) {
                        showPlaceholder(isError = false)
                    } else {
                        showResults(tracks)
                    }
                } else {
                    showPlaceholder(isError = true)
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                showPlaceholder(isError = true)
            }
        })
    }

    private fun showPlaceholder(isError: Boolean) {
        recyclerView.visibility = View.GONE
        historyLayout.visibility = View.GONE
        placeholderLayout.visibility = View.VISIBLE

        if (isError) {
            placeholderImage.setImageResource(R.drawable.vector_error)
            placeholderText.text = getString(R.string.error_message_header)
            placeholderTextSecond.visibility = View.VISIBLE
            refreshButton.visibility = View.VISIBLE
        } else {
            placeholderImage.setImageResource(R.drawable.vector_empty_search)
            placeholderText.text = getString(R.string.nothing_found)
            placeholderTextSecond.visibility = View.GONE
            refreshButton.visibility = View.GONE
        }
    }

    private fun showResults(tracks: List<Track>) {
        placeholderLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        adapter.updateList(tracks)
    }

    private fun showHistoryIfEmptyQuery() {
        if (searchEditText.text.isEmpty()) {
            showHistory()
        }
    }

    private fun showHistory() {
        val history = historyManager.getHistory()
        if (history.isEmpty()) {
            historyLayout.visibility = View.GONE
        } else {
            placeholderLayout.visibility = View.GONE
            recyclerView.visibility = View.GONE
            historyLayout.visibility = View.VISIBLE

            historyRecycler.visibility = View.VISIBLE
            historyTitle.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
        }
    }

    private fun updateHistory(show: Boolean = false) {
        val history = historyManager.getHistory()
        historyAdapter.updateList(history)
        if (show) showHistoryIfEmptyQuery()
    }

    private fun openAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(AudioPlayerActivity.EXTRA_TRACK, track)
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
        searchEditText.setText(searchQuery)

        if (searchQuery.isNotEmpty()) {
            searchTracksOnline(searchQuery)
        } else {
            updateHistory(show = true)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        searchEditText.clearFocus()
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "SEARCH_QUERY_KEY"
        private const val SEARCH_TIMEOUT: Long = 2000
    }
}
