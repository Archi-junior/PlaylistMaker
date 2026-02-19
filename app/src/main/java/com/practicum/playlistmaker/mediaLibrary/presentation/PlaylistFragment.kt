package com.practicum.playlistmaker.mediaLibrary.presentation

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.presentation.adapter.TrackAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment(R.layout.fragment_playlist) {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var menuBottomSheet: PlaylistMenuBottomSheet

    private val viewModel: PlaylistViewModel by viewModel {
        parametersOf(requireArguments().getLong(ARG_PLAYLIST_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupCallbacks()
        observeViewModel()
    }

    private fun setupCallbacks() {
        viewModel.onShareReady = { shareText ->
            sharePlaylist(shareText)
        }

        viewModel.onEmptyPlaylist = {
            showEmptyPlaylistMessage()
        }
    }

    private fun sharePlaylist(shareText: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_playlist)))
    }

    private fun showEmptyPlaylistMessage() {
        Toast.makeText(
            requireContext(),
            getString(R.string.no_tracks_to_share),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        tracksAdapter = TrackAdapter(
            onItemClick = { track ->
                openPlayer(track)
            },
            onItemLongClick = { track ->
                showDeleteTrackDialog(track)
            }
        )

        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRecyclerView.adapter = tracksAdapter
    }

    private fun showMenu(playlist: Playlist) {
        menuBottomSheet = PlaylistMenuBottomSheet.newInstance(playlist)

        menuBottomSheet.setOnShareClickListener {
            viewModel.onShareClicked()
        }

        menuBottomSheet.setOnEditClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_editPlaylistFragment,
                bundleOf("playlist" to playlist)
            )
        }

        menuBottomSheet.setOnDeleteClickListener {
            showDeletePlaylistDialog()
        }

        menuBottomSheet.show(parentFragmentManager, "playlist_menu")
    }

    private fun showDeletePlaylistDialog() {
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setView(R.layout.dialog_delete_playlist_confirmation)
            .create()

        dialog.show()
        dialog.findViewById<Button>(R.id.negativeButton)?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.positiveButton)?.setOnClickListener {
            viewModel.onDeletePlaylistClicked()
            dialog.dismiss()
        }

    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: PlaylistScreenState) {
        when (state) {
            is PlaylistScreenState.Content -> showContent(state)
            is PlaylistScreenState.Loading -> showLoading()
            is PlaylistScreenState.Error -> showError()
        }
    }

    private fun showContent(state: PlaylistScreenState.Content) {
        binding.progressBar.visibility = View.GONE
        binding.contentGroup.visibility = View.VISIBLE

        bindPlaylistInfo(state.playlist, state.totalDuration)
        tracksAdapter.submitList(state.tracks)

        binding.shareButton.setOnClickListener {
            viewModel.onShareClicked()
        }

        binding.menuButton.setOnClickListener {
            showMenu(state.playlist)
        }
    }

    private fun bindPlaylistInfo(playlist: Playlist, totalDuration: String) {
        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description ?: ""
        binding.playlistDescription.visibility = if (playlist.description.isNullOrBlank()) View.GONE else View.VISIBLE

        binding.tracksCount.text = resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount,
            playlist.tracksCount
        )
        binding.totalDuration.text = totalDuration

        if (playlist.coverPath != null) {
            Glide.with(this)
                .load(playlist.coverPath)
                .transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_corner_radius))
                )
                .placeholder(R.drawable.ic_image_placeholder_34)
                .into(binding.coverImage)
        } else {
            binding.coverImage.setImageResource(R.drawable.ic_image_placeholder_34)
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentGroup.visibility = View.GONE
    }

    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.contentGroup.visibility = View.GONE
    }

    private fun showDeleteTrackDialog(track: Track) {
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setView(R.layout.dialog_delete_track_confirmation)
            .create()

        dialog.show()

        dialog.findViewById<Button>(R.id.positiveButton)?.setOnClickListener {
            viewModel.onDeleteTrackClicked(track)
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.negativeButton)?.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_playerFragment,
            bundleOf("track" to track)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlistId"

        fun newInstance(playlistId: Long): PlaylistFragment {
            return PlaylistFragment().apply {
                arguments = bundleOf(ARG_PLAYLIST_ID to playlistId)
            }
        }
    }
}