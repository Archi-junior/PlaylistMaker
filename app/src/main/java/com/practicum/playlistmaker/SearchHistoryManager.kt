import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.Track
import androidx.core.content.edit

class SearchHistoryManager(context: Context) {
    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val maxSize = 10

    fun getHistory(): List<Track> {
        val json = prefs.getString("tracks", null) ?: return emptyList()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > maxSize) history.removeAt(history.lastIndex)
        save(history)
    }

    fun clear() {
        prefs.edit { remove("tracks") }
    }

    private fun save(history: List<Track>) {
        val json = gson.toJson(history)
        prefs.edit { putString("tracks", json) }
    }
}
