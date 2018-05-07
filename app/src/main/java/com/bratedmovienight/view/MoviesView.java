package com.bratedmovienight.view;

import com.bratedmovienight.data.MoviesRepository;
import com.bratedmovienight.model.Movie;

import java.util.List;

public interface MoviesView extends BaseView {
    void showMovies(List<Movie> movies);
    void clearList();
    void showContentLoading();
    void hideContentLoading();
    void showEmptyResultsView();
    void hideEmptyResultsView();
    void showErrorMessage(String errorMessage);
    void showErrorMessage(int messageResourceId);
    void onSortOrderChanged(@MoviesRepository.SortOrder String sortOrder);
    void stopRefreshing();
    boolean checkNetworkConnection();
}
