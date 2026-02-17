package com.practicum.playlistmaker.mediaLibrary.presentation

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewPlaylistViewModel by viewModel()

    private var selectedImageUri: Uri? = null
    private var hasUnsavedChanges = false

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            selectedImageUri = it
            loadImageToCover(it)
            hasUnsavedChanges = true
            checkCreateButtonState()
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

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.coverContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.nameInput.addTextChangedListener {
            hasUnsavedChanges = true
            checkCreateButtonState()
        }

        binding.descriptionInput.addTextChangedListener {
            hasUnsavedChanges = true
        }

        binding.backButton.setOnClickListener {
            handleBackPress()
        }

        binding.createButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val description = binding.descriptionInput.text.toString().trim()
            viewModel.createPlaylist(name, description, selectedImageUri)
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NewPlaylistState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.playlist_created, state.playlistName),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
                is NewPlaylistState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.playlist_create_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                NewPlaylistState.Idle -> { }
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

        binding.coverContainer.background = ContextCompat.getDrawable(requireContext(), R.drawable.cover_image_rounded)
    }

    private fun checkCreateButtonState() {
        val nameNotEmpty = binding.nameInput.text.toString().trim().isNotEmpty()
        binding.createButton.isEnabled = nameNotEmpty
    }

    private fun handleBackPress() {
        if (hasUnsavedChanges && (selectedImageUri != null ||
                    binding.nameInput.text.toString().isNotBlank() ||
                    binding.descriptionInput.text.toString().isNotBlank())) {

            showExitConfirmationDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun showExitConfirmationDialog() {
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setView(R.layout.dialog_exit_confirmation)
            .create()

        dialog.show()

        dialog.findViewById<Button>(R.id.positiveButton)?.setOnClickListener {
            findNavController().popBackStack()
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.negativeButton)?.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }
}