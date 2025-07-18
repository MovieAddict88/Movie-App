package my.cinemax.app.free.tmdb;

import com.google.gson.annotations.SerializedName;
import my.cinemax.app.free.entity.Poster;
import java.util.List;

public class TmdbMovieResponse {
    @SerializedName("results")
    private List<Poster> movies;

    public List<Poster> getMovies() {
        return movies;
    }
}
