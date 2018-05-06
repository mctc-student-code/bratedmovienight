package com.bratedmovienight.service;

import com.bratedmovienight.model.Movies;
import com.bratedmovienight.model.Reviews;
import com.bratedmovienight.model.Trailers;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesApiService {
    @GET("movie/{sort}")
    Single<Movies> getMovies(@Path("sort") String order, @Query("page") int page, @Query("language") String language);

    @GET("movie/{movie_id}/reviews")
    Single<Reviews> getReviews(@Path("movie_id") int movieId, @Query("page") int page, @Query("language") String language);

    @GET("movie/{movie_id}/videos")
    Single<Trailers> getTrailers(@Path("movie_id") int movieId, @Query("language") String language);
}