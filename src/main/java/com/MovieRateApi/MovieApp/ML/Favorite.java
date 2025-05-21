package com.MovieRateApi.MovieApp.ML;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Favorite {
    private String media_type;
    private int media_id;
    private boolean favorite;
}
