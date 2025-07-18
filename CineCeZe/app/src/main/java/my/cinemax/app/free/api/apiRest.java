package my.cinemax.app.free.api;

import my.cinemax.app.free.config.Global;
import my.cinemax.app.free.entity.Actor;
import my.cinemax.app.free.entity.ApiResponse;
import my.cinemax.app.free.entity.Category;
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Comment;
import my.cinemax.app.free.entity.Country;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Language;
import my.cinemax.app.free.entity.Plan;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Season;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Tamim on 28/09/2017.
 */

public interface apiRest {


    @GET("movie/popular?api_key=871c8ec045dba340e55b032a0546948c&language=en-US&page=1")
    Call<Data> homeData();

    @GET("search/movie?api_key=871c8ec045dba340e55b032a0546948c&language=en-US&query={query}&page=1&include_adult=false")
    Call<Data> searchData(@Path("query") String query);

    @GET("movie/{id}/credits?api_key=871c8ec045dba340e55b032a0546948c&language=en-US")
    Call<List<Actor>> getRolesByPoster(@Path("id") Integer id);

    @GET("person/popular?api_key=871c8ec045dba340e55b032a0546948c&language=en-US&page=1")
    Call<List<Actor>> getActorsList();

    @GET("movie/{id}?api_key=871c8ec045dba340e55b032a0546948c&language=en-US")
    Call<Poster> getPosterById(@Path("id") Integer id);

    @GET("movie/{id}/similar?api_key=871c8ec045dba340e55b032a0546948c&language=en-US&page=1")
    Call<List<Poster>> getRandomMoivies(@Path("id") Integer id);

    @GET("genre/movie/list?api_key=871c8ec045dba340e55b032a0546948c&language=en-US")
    Call<List<Genre>> getGenreList();

    @GET("discover/movie?api_key=871c8ec045dba340e55b032a0546948c&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&with_genres={genre}")
    Call<List<Poster>> getMoviesByFiltres(@Path("genre") Integer genre);

    @GET("movie/{id}/videos?api_key=871c8ec045dba340e55b032a0546948c&language=en-US")
    Call<List<Language>> getSubtitlesByPoster(@Path("id") Integer id);

}

