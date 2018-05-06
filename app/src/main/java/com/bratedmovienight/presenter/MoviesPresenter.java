package com.bratedmovienight.presenter;

import com.bratedmovienight.view.MoviesView;

public interface MoviesPresenter extends BasePresenter<MoviesView> {
    void fetchMovies(boolean forceLoad);
    void refreshList();
    void onSortOrderPreferencesChange();
    void onListScrolled();
    void onMovieChanged(int movieId);
}