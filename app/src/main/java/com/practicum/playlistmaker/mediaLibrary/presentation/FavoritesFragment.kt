    package com.practicum.playlistmaker.mediaLibrary.presentation

    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.lifecycle.Lifecycle
    import androidx.lifecycle.lifecycleScope
    import androidx.lifecycle.repeatOnLifecycle
    import androidx.navigation.fragment.findNavController
    import com.practicum.playlistmaker.R
    import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
    import com.practicum.playlistmaker.search.domain.models.Track
    import com.practicum.playlistmaker.search.presentation.adapter.TrackAdapter
    import kotlinx.coroutines.launch
    import org.koin.androidx.viewmodel.ext.android.viewModel

    class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

        private val viewModel: FavoritesViewModel by viewModel()
        private var _binding: FragmentFavoritesBinding? = null
        private val binding get() = _binding!!
        private lateinit var adapter: TrackAdapter

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            setupRecyclerView()
            observeViewModel()
        }

        private fun setupRecyclerView() {
            adapter = TrackAdapter { track ->
                openPlayer(track)
            }
            binding.favoriteTracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.favoriteTracksRecyclerView.adapter = adapter
        }

        private fun observeViewModel() {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collect { state ->
                        when (state) {
                            is FavoritesState.Empty -> showPlaceholder()
                            is FavoritesState.Content -> showFavorites(state.tracks)
                        }
                    }
                }
            }
        }

        private fun showPlaceholder() {
            binding.favoriteTracksRecyclerView.visibility = View.GONE
            binding.placeholderLayout.visibility = View.VISIBLE
        }

        private fun showFavorites(tracks: List<Track>) {
            binding.favoriteTracksRecyclerView.visibility = View.VISIBLE
            binding.placeholderLayout.visibility = View.GONE
            adapter.submitList(tracks)
        }

        private fun openPlayer(track: Track) {
            val bundle = Bundle().apply {
                putParcelable("track", track)
            }
            findNavController().navigate(R.id.action_mediaLibrary_to_player, bundle)
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        companion object {
            fun newInstance() = FavoritesFragment()
        }
    }
