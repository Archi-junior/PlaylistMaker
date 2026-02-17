package com.practicum.playlistmaker.mediaLibrary.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaLibraryBinding

class MediaLibraryFragment : Fragment(R.layout.fragment_media_library) {
    private var _binding: FragmentMediaLibraryBinding? = null
    private val binding get() = _binding!!
    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMediaLibraryBinding.bind(view)

        setupViewPager()
    }

    private fun setupViewPager() {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPager.adapter = MediaLibraryPagerAdapter(this)

        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.playlists)
                else -> getString(R.string.favorites)
            }
        }.apply { attach() }
    }

    override fun onDestroyView() {
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = MediaLibraryFragment()
    }
}