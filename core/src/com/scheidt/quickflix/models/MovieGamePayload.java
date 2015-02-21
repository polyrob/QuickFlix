package com.scheidt.quickflix.models;

/**
 * Created by NewRob on 2/8/2015.
 */
public class MovieGamePayload {
    private Movie mLeft;
    private Movie mRight;

    private Person correct;
    private Person wrong1;
    private Person wrong2;

    public Person getCorrect() {
        return correct;
    }

    public void setCorrect(Person correct) {
        this.correct = correct;
    }

    public Movie getmLeft() {
        return mLeft;
    }

    public void setmLeft(Movie mLeft) {
        this.mLeft = mLeft;
    }

    public Movie getmRight() {
        return mRight;
    }

    public void setmRight(Movie mRight) {
        this.mRight = mRight;
    }

    public Person getWrong1() {
        return wrong1;
    }

    public void setWrong1(Person wrong1) {
        this.wrong1 = wrong1;
    }

    public Person getWrong2() {
        return wrong2;
    }

    public void setWrong2(Person wrong2) {
        this.wrong2 = wrong2;
    }
}

