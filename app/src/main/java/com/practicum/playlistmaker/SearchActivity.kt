package com.practicum.playlistmaker

import ItunesApiService
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
    private lateinit var apiService: ItunesApiService

    private lateinit var placeholderLayout: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var placeholderTextSecond: TextView
    private lateinit var refreshButton: TextView

    private var searchQuery: String = ""

    companion object {
        private const val SEARCH_QUERY_KEY = "SEARCH_QUERY_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        backButton = findViewById(R.id.back_button)
        recyclerView = findViewById(R.id.tracks_recycler_view)

        placeholderLayout = findViewById(R.id.placeholder_layout)
        placeholderImage = findViewById(R.id.placeholder_image)
        placeholderText = findViewById(R.id.placeholder_text)
        placeholderTextSecond = findViewById(R.id.placeholder_text_second)
        refreshButton = findViewById(R.id.refresh_button)

        setupRetrofit()
        setupRecyclerView()
        setupSearchField()

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            clearButton.isVisible = false
            adapter.updateList(emptyList())
            hideKeyboard()
        }

        backButton.setOnClickListener {
            finish()
        }

        refreshButton.setOnClickListener {
            val currentQuery = searchEditText.text.toString().trim()
            if (currentQuery.isNotEmpty()) {
                searchQuery = currentQuery
                searchTracksOnline(searchQuery)
            }
        }
    }


    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ItunesApiService::class.java)
    }

    private fun showPlaceholder(isError: Boolean) {
        recyclerView.visibility = View.GONE
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
        recyclerView.visibility = View.VISIBLE
        adapter.updateList(tracks)
    }


    private fun setupRecyclerView() {
        adapter = TrackAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupSearchField() {
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchQuery = searchEditText.text.toString().trim()
                clearButton.isVisible = searchQuery.isNotEmpty()

                if (searchQuery.isNotEmpty()) {
                    searchTracksOnline(searchQuery)
                } else {
                    adapter.updateList(emptyList())
                }
                hideKeyboard()
                true
            } else {
                false
            }
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
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        searchEditText.clearFocus()
    }
}
