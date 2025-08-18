package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<ImageView>(R.id.arrow_back).setOnClickListener { finish() }

        findViewById<TextView>(R.id.layout_share_app).setOnClickListener { shareApp() }
        findViewById<TextView>(R.id.layout_contact_support).setOnClickListener { contactSupport() }
        findViewById<TextView>(R.id.user_agreement_layout).setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, getString(R.string.user_agreement_url).toUri())
            startActivity(browserIntent)
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
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}
