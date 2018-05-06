package com.bratedmovienight.data;

import com.bratedmovienight.model.Movie;

import java.util.List;

import io.reactivex.Single;

public interface MoviesDataSource {
    Single<List<Movie>> getMovies(boolean forceLoad, String language);
    Movie getMovie(int movieId);
    boolean canGetMoreMovies();
    void refreshList();
    void updateMovie(int movieId);
}