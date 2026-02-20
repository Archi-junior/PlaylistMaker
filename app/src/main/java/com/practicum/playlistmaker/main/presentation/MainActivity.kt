package com.practicum.playlistmaker.main.presentation

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateToolbar(destination.id)
            when (destination.id) {
                R.id.playerFragment -> binding.bottomNavigationView.isVisible= false
                R.id.newPlaylistFragment -> binding.bottomNavigationView.isVisible = false
                R.id.playlistFragment -> binding.bottomNavigationView.isVisible = false
                R.id.editPlaylistFragment -> binding.bottomNavigationView.isVisible = false
                else -> binding.bottomNavigationView.isVisible = true
            }
        }
        binding.backButton.setOnClickListener {
            navController.navigateUp()
        }
        setupKeyboardVisibilityListener()
    }

    private fun setupKeyboardVisibilityListener() {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keyboardHeight = screenHeight - rect.bottom

            if (keyboardHeight > screenHeight * 0.15) {
                binding.bottomNavigationView.isVisible = false
                binding.bottomDivider.isVisible = false
            } else {
                val currentDestination = navController.currentDestination?.id
                if (currentDestination != R.id.playerFragment && currentDestination != R.id.newPlaylistFragment && currentDestination != R.id.playlistFragment && currentDestination != R.id.editPlaylistFragment) {
                    binding.bottomNavigationView.isVisible = true
                    binding.bottomDivider.isVisible = true
                }
            }
        }
    }

    private fun updateToolbar(destinationId: Int) { //TODO: Перенести во фрагменты в будущем
        when (destinationId) {
            R.id.searchFragment -> {
                binding.titleText.isVisible = true
                binding.titleText.text = getString(R.string.search_header)
                binding.backButton.isVisible= false
            }

            R.id.mediaLibraryFragment -> {
                binding.titleText.isVisible = true
                binding.titleText.text = getString(R.string.media_library_screen_title)
                binding.backButton.isVisible = false
            }

            R.id.settingsFragment -> {
                binding.titleText.isVisible = true
                binding.titleText.text = getString(R.string.settings_title)
                binding.backButton.isVisible= false
            }

            R.id.playerFragment -> {
                binding.titleText.isVisible = false
                binding.backButton.isVisible = true
            }

            R.id.newPlaylistFragment -> {
                binding.titleText.isVisible = false
                binding.backButton.isVisible = false
            }

            R.id.playlistFragment -> {
                binding.titleText.isVisible = false
                binding.backButton.isVisible = false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}