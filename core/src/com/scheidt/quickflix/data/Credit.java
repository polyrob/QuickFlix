package com.scheidt.quickflix.data;

/**
 * Created by NewRob on 2/8/2015.
 */
public class Credit {

    private int personId;
    private int movieId;
    private int rank;

    public Credit(int personId, int movieId, int rank) {
        this.personId = personId;
        this.movieId = movieId;
        this.rank = rank;
    }


    public int getPersonId() {
        return personId;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getRank() { return rank; }
}
