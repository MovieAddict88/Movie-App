package my.cinemax.app.free.tmdb;

import my.cinemax.app.free.entity.Poster;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbService {
    @GET("movie/popular")
    Call<TmdbMovieResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<TmdbMovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<Poster> getMovieDetails(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("tv/popular")
    Call<TmdbMovieResponse> getPopularTv(@Query("api_key") String apiKey);

    @GET("tv/top_rated")
    Call<TmdbMovieResponse> getTopRatedTv(@Query("api_key") String apiKey);

    @GET("tv/{tv_id}")
    Call<Poster> getTvShowDetails(@Path("tv_id") int tvId, @Query("api_key") String apiKey);
}
