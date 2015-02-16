package com.scheidt.quickflix.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.scheidt.quickflix.models.GamePayload;
import com.scheidt.quickflix.models.Movie;
import com.scheidt.quickflix.models.Person;

import java.io.Serializable;
import java.util.*;

/**
 * Created by NewRob on 2/8/2015.
 */
public class CrossReference implements Serializable {
    private static final long serialVersionUID = -1549585679651669114L;

    public static final String FILENAME = "crs_ref.qfx";

    private ArrayList<Person> personList;
    private ArrayList<Movie> movieList;

    private Map<Person, List<Movie>> personLookup;
    private Map<Movie, List<Person>> movieLookup;


    public CrossReference() {
        this.personLookup = new HashMap<Person, List<Movie>>();
        this.movieLookup = new HashMap<Movie, List<Person>>();
    }

    public void initLists() {
        this.personList = new ArrayList<Person>();
        this.movieList = new ArrayList<Movie>();

        for (Person p : personLookup.keySet()) {
            personList.add(p);
        }

        for (Movie m : movieLookup.keySet()) {
            movieList.add(m);
        }
    }

    public int getPersonMapSize() {
        return personLookup.size();
    }

    public int getMovieMapSize() {
        return movieLookup.size();
    }

    public Set<Person> getAllPeople() {
        return personLookup.keySet();
    }

    public Set<Movie> getAllMovies() {
        return movieLookup.keySet();
    }

    public List<Person> getPeopleForMovie(Movie movie) {
        return movieLookup.get(movie);
    }


    public List<Movie> getMoviesForPerson(Person person) {
        return personLookup.get(person);
    }

    public void addRelationship(Person person, Movie movie) {
        /* add to personLookup */
        List<Movie> credits;
        if (personLookup.containsKey(person)) {
            credits = personLookup.get(person);
        } else {
            credits = new ArrayList<Movie>();
            personLookup.put(person, credits);
        }
        credits.add(movie);

        /* add to movieLookup */
        List<Person> cast;
        if (movieLookup.containsKey(movie)) {
            cast = movieLookup.get(movie);
        } else {
            cast = new ArrayList<Person>();
            movieLookup.put(movie, cast);
        }
        cast.add(person);
    }

    public Person getRandomPerson() {
        int num = MathUtils.random(personList.size());
        return personList.get(num);
    }


    public GamePayload getGamePayload(Person p1, int level) {
        /* get one of their movies at random */
        List<Movie> appearances = getMoviesForPerson(p1);
        int randMovieIndex = MathUtils.random(0, appearances.size()-1);
        Movie m = (Movie) appearances.get(randMovieIndex);
        List<Person> fullCast = getPeopleForMovie(m);
        Person p2;
        do {
            int randPersonIndex = MathUtils.random(0, fullCast.size()-1);
            p2 = (Person) fullCast.get(randPersonIndex);
        } while (p1 == p2); // dumb, but simplest way to do it for now
        GamePayload payload = new GamePayload();
        payload.p1 = p1;
        payload.p2 = p2;
        payload.correct = m;
        Gdx.app.log("GameLogic", "Shuffle complete. P1: " + p1.getName() + ", Movie: " + m.getTitle() + ", P2:" + p2.getName());


        /* get two wrong movies */
        Movie wrong1, wrong2;

        int rnd = MathUtils.random(0, level+50);
        if (level > rnd) {
            Gdx.app.log("Payload difficulty", "Hardest!");
            wrong1 = getMovieForOne(p1, p2);
            wrong2 = getMovieForOne(p1, p2);
        } else if (level > rnd/2) {
            Gdx.app.log("Payload difficulty","Medium");
            wrong1 = getMovieForOne(p1, p2);
            wrong2 = getMovieForNeither(p1, p2);
        } else {
            Gdx.app.log("Payload difficulty","Easy");
            wrong1 = getMovieForNeither(p1, p2);
            wrong2 = getMovieForNeither(p1, p2);
        }

        payload.wrong1 = wrong1;
        payload.wrong2 = wrong2;

        return payload;
    }

    private Movie getMovieForOne(Person p1, Person p2) {
        List<Movie> p1Films = getMoviesForPerson(p1);
        List<Person> cast;
        Movie m;
        do {
            int randMovieIndex = MathUtils.random(0, p1Films.size()-1);
            m = (Movie) p1Films.get(randMovieIndex);
        /* make sure p2 isn't in it */
            cast = getPeopleForMovie(m);
        } while (cast.contains(p2));
        return m;
    }

    private Movie getMovieForNeither(Person p1, Person p2) {
        List<Person> cast;
        Movie m;
        do {
            int randMovieIndex = MathUtils.random(0, movieList.size() - 1);
            m = movieList.get(randMovieIndex);
            cast = getPeopleForMovie(m);

        } while (cast.contains(p1) || cast.contains(p2));
        return m;
    }
}
