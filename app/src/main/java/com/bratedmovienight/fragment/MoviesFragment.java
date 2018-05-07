package com.bratedmovienight.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bratedmovienight.R;
import com.bratedmovienight.adapter.MoviesAdapter;
import com.bratedmovienight.data.MovieContract;
import com.bratedmovienight.data.MoviesRepository;
import com.bratedmovienight.data.Settings;
import com.bratedmovienight.data.SharedPreferencesRepository;
import com.bratedmovienight.data.SharedPreferencesRepositoryImpl;
import com.bratedmovienight.model.Movie;
import com.bratedmovienight.presenter.MoviesPresenter;
import com.bratedmovienight.presenter.MoviesPresenterImpl;
import com.bratedmovienight.util.NetworkUtils;
import com.bratedmovienight.view.MoviesView;

import java.util.List;

import de.mateware.snacky.Snacky;

import static com.bratedmovienight.data.MoviesRepository.FAVORITES;
import static com.bratedmovienight.data.MoviesRepository.POPULAR;
import static com.bratedmovienight.data.MoviesRepository.TOP_RATED;

public class MoviesFragment extends Fragment implements MoviesView, SharedPreferences.OnSharedPreferenceChangeListener {
    // region CONSTANTS
    private static final String STATE_VISIBLE_POSITION = "position";
    // endregion

    // region VARIABLES
    @BindView(R.id.movies_recycler_view)
    RecyclerView moviesRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_text_view)
    TextView mEmptyTextView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.movies_header_text_view)
    TextView headerTextView;

    private Unbinder mUnbinder;
    private GridLayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;
    private MoviesPresenter mPresenter;
    private OnMovieSelectedListener mListener;
    private int mCurrentVisiblePosition = -1;
    private MoviesDbContentObserver mContentObserver;
    // endregion

    // region CONSTRUCTORS
    public MoviesFragment() {
        // Required empty public constructor
    }
    // endregion

    // region LISTENERS
    public interface OnMovieSelectedListener {
        void onMovieSelected(Movie movie, ImageView sharedImageView);
    }
    // endregion

    // region LIFE CYCLE METHODS
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_VISIBLE_POSITION)) {
                mCurrentVisiblePosition = savedInstanceState.getInt(STATE_VISIBLE_POSITION);
            }
        }

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);

        SharedPreferencesRepository preferencesRepository = new SharedPreferencesRepositoryImpl(getActivity());
        mPresenter = new MoviesPresenterImpl(this, preferencesRepository,
                MoviesRepository.getInstance(getActivity()), Settings.getInstance());

        setupUI();
        setupAdapter();

        mPresenter.start();

        mContentObserver = new MoviesDbContentObserver(new Handler(Looper.getMainLooper()));

        getContext().getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI,
                true, mContentObserver);

        return view;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onActivityAttached(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        onActivityAttached(activity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPresenter.fetchMovies(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);

        getContext().getContentResolver().unregisterContentObserver(mContentObserver);

        mPresenter.stop();
        mUnbinder.unbind();
    }
    // endregion

    // region STATE METHODS
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mAdapter.getItemCount() > 0) {
            outState.putInt(STATE_VISIBLE_POSITION, mLayoutManager.findFirstVisibleItemPosition());
        }

        super.onSaveInstanceState(outState);
    }
    // endregion

    // region SHARED PREFERENCES CHANGE LISTENER
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_orders_key))) {
            mPresenter.onSortOrderPreferencesChange();
        }
    }
    // endregion

    // region MOVIES VIEW METHODS
    @Override
    public void showMovies(List<Movie> movies) {
        if (mAdapter != null) {
            mAdapter.addMovies(movies);

            if (mAdapter.getItemCount() == 0) {
                showEmptyResultsView();
            } else {
                hideEmptyResultsView();
            }

            if (mCurrentVisiblePosition != -1) {
                moviesRecyclerView.scrollToPosition(mCurrentVisiblePosition);
                mCurrentVisiblePosition = -1;
            }
        }

        refreshLayout.setRefreshing(false);
    }

    @Override
    public void clearList() {
        if (mAdapter != null) {
            mAdapter.clear();
            moviesRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void showContentLoading() {
        showProgressBar();
    }

    @Override
    public void hideContentLoading() {
        hideProgressBar();
    }

    @Override
    public void showEmptyResultsView() {
        if (mEmptyTextView.getVisibility() == View.GONE) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideEmptyResultsView() {
        if (mEmptyTextView.getVisibility() == View.VISIBLE) {
            mEmptyTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Snacky.builder()
                .setActivty(getActivity())
                .setText(errorMessage)
                .error()
                .show();
    }

    @Override
    public void showErrorMessage(int messageResourceId) {
        showErrorMessage(getString(messageResourceId));
    }

    @Override
    public void onSortOrderChanged(@MoviesRepository.SortOrder String sortOrder) {
        switch (sortOrder) {
            case POPULAR:
                headerTextView.setText(getString(R.string.pref_sort_orders_label_popular));

                break;
            case TOP_RATED:
                headerTextView.setText(getString(R.string.pref_sort_orders_label_top_rated));

                break;
            case FAVORITES:
                headerTextView.setText(getString(R.string.pref_sort_orders_label_favorites));

                break;
        }
    }

    @Override
    public boolean checkNetworkConnection() {
        return NetworkUtils.isNetworkAvailable(getActivity());
    }

    @Override
    public void stopRefreshing() {
        refreshLayout.setRefreshing(false);
    }
    // endregion

    // region PRIVATE METHODS
    private void setupUI() {
        mLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns());
        moviesRecyclerView.setLayoutManager(mLayoutManager);

        moviesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            final int visibleThreshold = 3;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int totalItemCount = mLayoutManager.getItemCount();
                    int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                    if ((lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                        if (NetworkUtils.isNetworkAvailable(getActivity())) {
                            mPresenter.onListScrolled();
                        }
                    }
                }
            }
        });

        refreshLayout.setColorSchemeResources(R.color.colorAccent);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshList();
            }
        });
    }

    private void setupAdapter() {
        mAdapter = new MoviesAdapter();

        mAdapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Movie movie = mAdapter.getMovie(position);

                if (movie != null && mListener != null) {
                    ImageView posterImageView = (ImageView) itemView.findViewById(R.id.poster_image_view);
                    mListener.onMovieSelected(movie, posterImageView);
                }
            }
        });

        moviesRecyclerView.setAdapter(mAdapter);
    }

    private void onActivityAttached(Context context) {
        if (null != mListener) {
            return;
        }

        try {
            mListener = (OnMovieSelectedListener) context;
        } catch (ClassCastException ignored) {
        }

        if (mListener == null) {
            mListener = (OnMovieSelectedListener) getTargetFragment();
        }
    }

    private void showProgressBar() {
        if (progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 300;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;

        if (nColumns < 2) {
            return 2;
        }

        return nColumns;
    }
    // endregion

    // region INNER CLASSES
    private class MoviesDbContentObserver extends ContentObserver {
        public MoviesDbContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            String movieIdPath = uri.getLastPathSegment();

            if (!TextUtils.isEmpty(movieIdPath)) {
                try {
                    int movieId = Integer.parseInt(movieIdPath);

                    if (mAdapter != null) {
                        mAdapter.updateMovie(movieId);
                        mPresenter.onMovieChanged(movieId);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }
    // endregion
}
