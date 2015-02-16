package com.scheidt.quickflix.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by NewRob on 2/7/2015.
 */
public class Person implements Serializable{
    private int id;
    private String name;
    private int birthYear;
    private float popularity;
    //private List<Integer> movies;
    private String imgPath;
    private String imdbId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    //    public List<Integer> getMovies() {
//        return movies;
//    }
//
//    public void setMovies(List<Integer> movies) {
//        this.movies = movies;
//    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id != person.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
