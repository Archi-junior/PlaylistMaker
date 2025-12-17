package com.practicum.playlistmaker.settings.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        observeViewModel()
    }

    private fun initUI() {
        binding.arrowBack.setOnClickListener { finish() }

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
        // Theme
        viewModel.isDarkTheme.observe(this) { isDark ->
            binding.themeSwitcher.isChecked = isDark
            applyTheme(isDark)
        }

        // Events
        viewModel.events.observe(this) { event ->
            when (event) {
                SettingsEvent.ShareApp -> shareApp()
                SettingsEvent.ContactSupport -> contactSupport()
                is SettingsEvent.OpenUserAgreement -> openUserAgreement(event.url)
            }
        }
    }

    private fun applyTheme(isDark: Boolean) {
        val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
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
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun openUserAgreement(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        if (browserIntent.resolveActivity(packageManager) != null) {
            startActivity(browserIntent)
        }
    }
}