    package com.practicum.playlistmaker.medialibrary.presentation

    import androidx.fragment.app.Fragment
    import com.practicum.playlistmaker.R
    import org.koin.androidx.viewmodel.ext.android.viewModel

    class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

        private val viewModel: FavoritesViewModel by viewModel()

        companion object {
            fun newInstance() = FavoritesFragment()
        }
    }
