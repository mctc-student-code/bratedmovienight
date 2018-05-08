package com.bratedmovienight.Database;

import com.google.firebase.database.IgnoreExtraProperties;

// class to store movie data into firebase database
@IgnoreExtraProperties
public class MovieFirebase {

    public String id;
    public String name;
    public String posterPath;
    public String year;
    public Boolean wishlist;
    public Boolean watchlist;
    public Number vote_average;

    public MovieFirebase(){

    }

    public MovieFirebase(String id, String name, String posterPath, String year, Boolean wishlist, Boolean watchlist, Number vote_average) {
        this.id = id;
        this.name = name;
        this.posterPath = posterPath;
        this.year = year;
        this.wishlist = wishlist;
        this.watchlist = watchlist;
        this.vote_average = vote_average;
    }


}