package com.practicum.playlistmaker.mediaLibrary.presentation

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediaLibraryPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> FavoritesFragment.newInstance()
        else -> PlaylistsFragment.newInstance()
    }
}