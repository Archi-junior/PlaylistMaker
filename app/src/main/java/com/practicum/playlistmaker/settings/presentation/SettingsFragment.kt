package com.practicum.playlistmaker.settings.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeChanged(isChecked)
        }

        binding.layoutShareApp.setOnClickListener {
            viewModel.onShareAppClicked()
        }

        binding.layoutContactSupport.setOnClickListener {
            viewModel.onContactSupportClicked()
        }

        binding.userAgreementLayout.setOnClickListener {
            viewModel.onUserAgreementClicked(getString(R.string.user_agreement_url))
        }
    }

    private fun observeViewModel() {
        viewModel.isDarkTheme.observe(viewLifecycleOwner) { isDark ->
            binding.themeSwitcher.isChecked = isDark
        }

        viewModel.events.observe(viewLifecycleOwner) { event ->
            when (event) {
                SettingsEvent.ShareApp -> shareApp()
                SettingsEvent.ContactSupport -> contactSupport()
                is SettingsEvent.OpenUserAgreement -> openUserAgreement(event.url)
            }
        }
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_chooser_title)))
    }

    private fun contactSupport() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun openUserAgreement(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        if (browserIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(browserIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}