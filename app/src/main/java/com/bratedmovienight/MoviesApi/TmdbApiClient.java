package com.bratedmovienight.MoviesApi;

import com.bratedmovienight.Helpers.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TmdbApiClient {

    private static final String TMDB_BASE_URL = Constants.TMDB_BASE_URL;
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {
        if (retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(TMDB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}