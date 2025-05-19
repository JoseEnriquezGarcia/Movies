package com.MovieRateApi.MovieApp.ML;

import java.util.List;

public class Result<T> {
    public int page;
    public List<T> results;
    public int total_pages;
    public int total_results;
}
