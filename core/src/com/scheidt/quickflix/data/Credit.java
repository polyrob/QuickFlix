package com.scheidt.quickflix.data;

/**
 * Created by NewRob on 2/8/2015.
 */
public class Credit {

    private int personId;
    private int movieId;

    public Credit(int personId, int movieId) {
        this.personId = personId;
        this.movieId = movieId;
    }


    public int getPersonId() {
        return personId;
    }

    public int getMovieId() {
        return movieId;
    }
}
