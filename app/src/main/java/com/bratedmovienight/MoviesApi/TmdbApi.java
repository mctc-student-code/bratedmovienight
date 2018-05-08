package com.bratedmovienight.MoviesApi;


import com.bratedmovienight.Models.Movie;
import com.bratedmovienight.Models.ResponseMultiSearch;
import com.bratedmovienight.Models.ResponsePopularMovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

// Retrofit interface with methods to call TMDB Api service

public interface TmdbApi {
    // movie
    @GET("movie/popular")
    Call<ResponsePopularMovies> getPopularMovies(@Query("api_key") String apiKey, @Query("page") int page);

    @GET("movie/{id}")
    Call<Movie> getMovieDetails(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/similar")
    Call<ResponsePopularMovies> getSimilarMovies(@Path("id") int movieId, @Query("api_key") String apiKey, @Query("page") int page);

    @GET("movie/{id}/recommendations")
    Call<ResponsePopularMovies> getRecommendedMovies(@Path("id") int movieId, @Query("api_key") String apiKey, @Query("page") int page);

    @GET("movie/upcoming")
    Call<ResponsePopularMovies> getUpcomingMovies(@Query("api_key") String apiKey, @Query("page") int page);

    // search

    @GET("search/multi")
    Call<ResponseMultiSearch> getSearchResults(@Query("api_key") String apiKey, @Query("query") String query, @Query("page") int page);}
