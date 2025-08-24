import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.Track
import androidx.core.content.edit

class SearchHistoryManager(context: Context) {

    companion object {
        private const val MAX_SIZE = 10
        private const val PREFS_NAME = "search_history"
        private const val KEY_TRACKS = "tracks"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getHistory(): List<Track> {
        val json = prefs.getString(KEY_TRACKS, null) ?: return emptyList()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > MAX_SIZE) history.removeAt(history.lastIndex)
        save(history)
    }

    fun clear() {
        prefs.edit { remove(KEY_TRACKS) }
    }

    private fun save(history: List<Track>) {
        val json = gson.toJson(history)
        prefs.edit { putString(KEY_TRACKS, json) }
    }
}
