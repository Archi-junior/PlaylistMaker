package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backImage = findViewById<ImageView>(R.id.arrow_back)
        backImage.setOnClickListener {
            finish()
        }
    }
}