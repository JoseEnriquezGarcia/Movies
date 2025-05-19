package com.MovieRateApi.MovieApp.ML;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class Movie {
    private String title;
    private String original_title;
    private String overview;
    private Date release_date;
    private String poster_path;
    private String original_language;
    private int vote_average;
}
