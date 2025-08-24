import com.practicum.playlistmaker.ItunesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("search?entity=song")
    fun searchTracks(@Query("term") text: String): Call<ItunesResponse>
}
