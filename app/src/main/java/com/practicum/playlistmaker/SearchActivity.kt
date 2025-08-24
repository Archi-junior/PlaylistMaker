package com.practicum.playlistmaker

import ItunesApiService
import SearchHistoryManager
import android.content.Context
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

    companion object {
        private const val SEARCH_QUERY_KEY = "SEARCH_QUERY_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        historyLayout = findViewById(R.id.history_layout)

        // элементы поиска
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        backButton = findViewById(R.id.back_button)
        recyclerView = findViewById(R.id.tracks_recycler_view)

        // история
        historyTitle = findViewById(R.id.history_title)
        historyRecycler = findViewById(R.id.history_recycler)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        historyManager = SearchHistoryManager(this)

        // плейсхолдеры
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
            showHistory()
            hideKeyboard()
        }

        backButton.setOnClickListener { finish() }

        refreshButton.setOnClickListener {
            if (searchQuery.isNotEmpty()) searchTracksOnline(searchQuery)
        }

        clearHistoryButton.setOnClickListener {
            historyManager.clear()
            updateHistory()
        }

        updateHistory()
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ItunesApiService::class.java)
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(emptyList()) { track ->
            historyManager.addTrack(track)
            updateHistory()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupHistoryRecycler() {
        historyAdapter = TrackAdapter(emptyList()) { track ->
            historyManager.addTrack(track)
            updateHistory()
            searchEditText.setText(track.trackName)
            searchEditText.setSelection(track.trackName.length)
            searchQuery = track.trackName
            searchTracksOnline(searchQuery)
        }
        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter
    }

    private fun setupSearchField() {
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
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
                    showHistory()
                }
                hideKeyboard()
                true
            } else false
        }
    }

    private fun searchTracksOnline(query: String) {
        apiService.searchTracks(query).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(call: Call<ItunesResponse>, response: Response<ItunesResponse>) {
                if (response.isSuccessful) {
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
                showPlaceholder(isError = true)
            }
        })
    }

    private fun showPlaceholder(isError: Boolean) {
        recyclerView.visibility = View.GONE
        historyLayout.visibility = View.GONE
        historyRecycler.visibility = View.GONE
        historyTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
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
        placeholderLayout.visibility = View.GONE
        historyRecycler.visibility = View.GONE
        historyTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        adapter.updateList(tracks)
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


    private fun updateHistory() {
        val history = historyManager.getHistory()
        historyAdapter.updateList(history)
        showHistory()
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
            updateHistory()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        searchEditText.clearFocus()
    }
}
