package com.practicum.playlistmaker.mediaLibrary.presentation

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class EditPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null

    private val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(requireArguments().getParcelable<Playlist>(ARG_PLAYLIST))
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            selectedImageUri = it
            loadImageToCover(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupUI()
        setupListeners()
        observeViewModel()
    }
    private fun setupToolbar() {
        binding.titleText.text = getString(R.string.edit_playlist)
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupUI() {
        binding.createButton.text = getString(R.string.save)

        viewModel.playlistData.observe(viewLifecycleOwner) { playlist ->
            binding.nameInput.setText(playlist.name)
            binding.descriptionInput.setText(playlist.description ?: "")

            if (playlist.coverPath != null) {
                loadImageToCover(playlist.coverPath)
            }
        }
    }

    private fun loadImageToCover(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .transform(
                CenterCrop(),
                RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_corner_radius))
            )
            .into(binding.coverImage)

        binding.coverPlaceholder.visibility = View.GONE
        binding.coverImage.visibility = View.VISIBLE
    }

    private fun loadImageToCover(path: String) {
        Glide.with(this)
            .load(File(path))
            .transform(
                CenterCrop(),
                RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_corner_radius))
            )
            .into(binding.coverImage)

        binding.coverPlaceholder.visibility = View.GONE
        binding.coverImage.visibility = View.VISIBLE
    }

    private fun setupListeners() {
        binding.coverContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkCreateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.createButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val description = binding.descriptionInput.text.toString().trim()
            viewModel.updatePlaylist(name, description, selectedImageUri)
        }
    }

    private fun checkCreateButtonState() {
        val nameNotEmpty = binding.nameInput.text.toString().trim().isNotEmpty()
        binding.createButton.isEnabled = nameNotEmpty
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NewPlaylistState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.playlist_updated, state.playlistName),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
                is NewPlaylistState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.playlist_update_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PLAYLIST = "playlist"

        fun newInstance(playlist: Playlist): EditPlaylistFragment {
            return EditPlaylistFragment().apply {
                arguments = bundleOf(ARG_PLAYLIST to playlist)
            }
        }
    }
}