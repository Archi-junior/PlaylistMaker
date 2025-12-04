package com.practicum.playlistmaker.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.presentation.di.ServiceLocator

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.themeSwitcher.isChecked = ServiceLocator.themeInteractor.isDark()
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            ServiceLocator.themeInteractor.saveTheme(isChecked)
            applyTheme(isChecked)
        }
        binding.layoutShareApp.setOnClickListener { shareApp() }
        binding.layoutContactSupport.setOnClickListener { contactSupport() }
        binding.userAgreementLayout.setOnClickListener { openUserAgreement() }

        binding.arrowBack.setOnClickListener { finish() }
    }

    private fun applyTheme(isDark: Boolean) {
        val mode = if (isDark) {
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
        } else {
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        }
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(mode)
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

    private fun openUserAgreement() {
        val url = getString(R.string.user_agreement_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        if (browserIntent.resolveActivity(packageManager) != null) {
            startActivity(browserIntent)
        }
    }
}