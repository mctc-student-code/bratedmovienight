package com.bratedmovienight.Fragments;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bratedmovienight.Adapters.MainAdapter;
import com.bratedmovienight.Models.Movie;
import com.bratedmovienight.R;
import com.bratedmovienight.Models.ResponsePopularMovies;
import com.bratedmovienight.MoviesApi.TmdbApi;
import com.bratedmovienight.MoviesApi.TmdbApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Main fragment for Movies
public class MainFragment extends Fragment{

    private final String TAG = "Main-Fragment";

    private Boolean isTvShowSelected = false;

    private RecyclerView mFragmentRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private MainAdapter mMainRecycleViewAdapter;
    private List<Movie> mMovies;
    private Call<ResponsePopularMovies> callPopularMovies;

    // fields requered to make "endless" scrolling of results in RecycleView
    private int resultsPage = 1;
    private int visibleThreshhold = 5;
    private int previousTotal = 0;
    private boolean isLastPage = false;
    private boolean isLoading = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        mFragmentRecyclerView = (RecyclerView)view.findViewById(R.id.main_fragment_rv);

        mMovies = new ArrayList<>();
        Log.d(TAG, "onCreateView: isTvShowSelected: " + isTvShowSelected);

        mMainRecycleViewAdapter= new MainAdapter(getContext(), mMovies);
        mFragmentRecyclerView.setAdapter(mMainRecycleViewAdapter);

        mGridLayoutManager = new GridLayoutManager(getContext(), 2);

        mFragmentRecyclerView.setLayoutManager(mGridLayoutManager);

        mFragmentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){

                int visibleItems = mGridLayoutManager.getChildCount();
                int totalItems = mGridLayoutManager.getItemCount();
                int firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();

                //if totalItems count is zero and previousItems count is not - invalidate list
                if (totalItems<previousTotal){
                    Log.d(TAG, "List invalidated");
                    resultsPage = 1;
                    previousTotal = totalItems;
                    if (totalItems == 0){
                        isLoading=true;
                    }
                }
                // stop loading
                if(isLoading){
                    if(totalItems>previousTotal){
                        isLoading = false;
                        previousTotal = totalItems;
                    }
                }
                // continue loading data

            loadPopularMovies();
        }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        //mMainRecycleViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Entered onDestroy");
        super.onDestroy();
        if (callPopularMovies!=null){
            callPopularMovies.cancel();
        }
    }

    // load popular movies
    public void loadPopularMovies(){
        Log.d(TAG, "Started loading popular movies");
        // stop load more if last page already
        if (isLastPage){
            return;
        }
        else{
            TmdbApi tmdbService = TmdbApiClient.getRetrofit().create(TmdbApi.class);
            callPopularMovies = tmdbService.getPopularMovies(getString(R.string.tmdb_api_key), resultsPage);
            callPopularMovies.enqueue(new Callback<ResponsePopularMovies>() {
                @Override
                public void onResponse(Call<ResponsePopularMovies> call, Response<ResponsePopularMovies> response) {
                    if (!response.isSuccessful()){
                        callPopularMovies = call.clone();
                        callPopularMovies.enqueue(this);
                        return;

                    }
                    if (response.body() == null){
                        Toast.makeText(getActivity().getApplicationContext(), "No data loaded", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        for (Movie movie: response.body().getResults()){
                            if (movie != null && movie.getPoster() != null){
                                mMovies.add(movie);
                            }
                        }
                        mMainRecycleViewAdapter.notifyDataSetChanged();

                        if (resultsPage==response.body().getTotalPages()){
                            isLastPage=true;
                        }
                        resultsPage +=1;
                    }

                }

                @Override
                public void onFailure(Call<ResponsePopularMovies> call, Throwable t) {
                    Toast.makeText(getActivity().getApplicationContext(), "No data available", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }


}