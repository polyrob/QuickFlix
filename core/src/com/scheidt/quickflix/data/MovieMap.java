package com.scheidt.quickflix.data;

import com.scheidt.quickflix.models.Movie;

import java.util.HashMap;

/**
 * Created by NewRob on 2/7/2015.
 */
public class MovieMap extends HashMap<Integer, Movie> {

    public static final String FILENAME = "movie_data.qfx";

    public boolean isMoviePresent(int id) {
        return containsKey(id) ? true : false;
    }


}
