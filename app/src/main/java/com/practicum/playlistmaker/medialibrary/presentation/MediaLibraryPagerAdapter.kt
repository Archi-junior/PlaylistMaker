    package com.practicum.playlistmaker.medialibrary.presentation

    import androidx.fragment.app.Fragment
    import androidx.fragment.app.FragmentActivity
    import androidx.viewpager2.adapter.FragmentStateAdapter

    class MediaLibraryPagerAdapter(
        fragment: Fragment
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment =
            when (position) {
                0 -> PlaylistsFragment.newInstance()
                else -> FavoritesFragment.newInstance()
            }
    }
