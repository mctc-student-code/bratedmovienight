package com.bratedmovienight.Models;

import com.google.gson.annotations.SerializedName;


public class Genre {

    @SerializedName("id")
    private int genreId;
    @SerializedName("name")
    private String genreName;

    public Genre(Integer genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}