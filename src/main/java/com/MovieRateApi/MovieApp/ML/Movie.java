package com.MovieRateApi.MovieApp.ML;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class Movie {
    private int id;
    private String title;
    private String original_title;
    private String overview;
    private String release_date;
    private String poster_path;
    private String backdrop_path;
    private String original_language;
    private float vote_average;
}
