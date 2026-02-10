package com.practicum.playlistmaker.main.presentation

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
                R.id.playerFragment -> binding.bottomNavigationView.visibility = View.GONE
                else -> binding.bottomNavigationView.visibility = View.VISIBLE
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
                binding.bottomNavigationView.visibility = View.GONE
                binding.bottomDivider.visibility = View.GONE
            } else {
                val currentDestination = navController.currentDestination?.id
                if (currentDestination != R.id.playerFragment) {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.bottomDivider.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateToolbar(destinationId: Int) { //TODO: Перенести во фрагменты в будущем
        when (destinationId) {
            R.id.searchFragment -> {
                binding.titleText.visibility = View.VISIBLE
                binding.titleText.text = getString(R.string.search_header)
                binding.backButton.visibility = View.GONE
            }
            R.id.mediaLibraryFragment -> {
                binding.titleText.visibility = View.VISIBLE
                binding.titleText.text = getString(R.string.media_library_screen_title)
                binding.backButton.visibility = View.GONE
            }
            R.id.settingsFragment -> {
                binding.titleText.visibility = View.VISIBLE
                binding.titleText.text = getString(R.string.settings_title)
                binding.backButton.visibility = View.GONE
            }
            R.id.playerFragment -> {
                binding.titleText.visibility = View.GONE
                binding.backButton.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}