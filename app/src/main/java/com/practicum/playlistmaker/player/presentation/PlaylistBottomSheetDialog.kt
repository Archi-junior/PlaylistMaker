package com.practicum.playlistmaker.player.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.DialogPlaylistSelectionBinding
import com.practicum.playlistmaker.mediaLibrary.presentation.PlaylistSelectionAdapter
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: DialogPlaylistSelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistSelectionViewModel by viewModel {
        parametersOf(requireArguments().getParcelable<Track>("track")!!)
    }
    private lateinit var adapter: PlaylistSelectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPlaylistSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = PlaylistSelectionAdapter { playlist ->
            viewModel.onPlaylistSelected(playlist)
        }
        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsRecyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_player_to_newPlaylist)
            dismiss()
        }
    }

    private fun observeViewModel() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapter.submitList(playlists)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TRACK = "track"

        fun newInstance(track: Track): PlaylistBottomSheetDialog {
            return PlaylistBottomSheetDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TRACK, track)
                }
            }
        }
    }
}