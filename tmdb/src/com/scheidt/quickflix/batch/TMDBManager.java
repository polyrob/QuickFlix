package com.scheidt.quickflix.batch;

import com.google.common.base.Strings;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;
import com.omertron.themoviedbapi.model.PersonCredit;
import com.omertron.themoviedbapi.model.PersonType;
import com.omertron.themoviedbapi.results.TmdbResultsList;
import com.scheidt.quickflix.models.Movie;
import com.scheidt.quickflix.models.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TMDBManager {
    public static String IMG_URL_LG = "https://image.tmdb.org/t/p/w185";
    public static String IMG_URL_SM = "https://image.tmdb.org/t/p/w92";

    TheMovieDbApi api;
    private String apikey;
    private boolean isInit;

    public TMDBManager(String apikey) {
        this.apikey = apikey;
    }

    public void init() throws MovieDbException, IOException {
        System.out.println("Initializing TMDB API....");
        api = new TheMovieDbApi(apikey);
        System.out.println("Initializing successful.");
        isInit = true;
    }

    public List<Person> getPeople(List<Integer> peopleList) throws MovieDbException, IOException {
        if (!isInit) init();

        List<Person> returnList = new ArrayList<Person>() ;

        System.out.println("Obtaining Person data from TMDB...");
        for (Integer personId : peopleList) {
            com.omertron.themoviedbapi.model.Person tmdbPerson = api.getPersonInfo(personId);
            Person p = new Person();
            p.setId(tmdbPerson.getId());
            p.setName(tmdbPerson.getName());
            p.setBirthYear(Strings.isNullOrEmpty(tmdbPerson.getBirthday()) ? 1900 : Integer.parseInt(tmdbPerson.getBirthday().substring(0, 4)));
            p.setPopularity(tmdbPerson.getPopularity());
            p.setImgPath(tmdbPerson.getProfilePath());
            p.setImdbId(tmdbPerson.getImdbId());

//            /* get list of movieIds for which the person was part of the CAST */
//            TmdbResultsList<PersonCredit> tmdbResultsList = api.getPersonCredits(tmdbPerson.getId());
//            List<PersonCredit> credits = tmdbResultsList.getResults();
//            List<Integer> creditList = new ArrayList<Integer>();
//            for (PersonCredit credit : credits) {
//                /* if they are cast and don't already have a credit for the same film, add them */
//                if (credit.getPersonType().equals(PersonType.CAST) && !creditList.contains(credit.getMovieId())) creditList.add(credit.getMovieId());
//            }
//            p.setMovies(creditList);

            returnList.add(p);
        }
        System.out.println("Obtaining Person data complete.");

        return returnList;
    }

    public List<Movie> getMovies(List<Integer> movieIds) throws MovieDbException, IOException {
        List<String> strIds = new ArrayList<String>();
        for (Integer i : movieIds) {
            strIds.add(String.valueOf(i));
        }
        return getMovies(strIds, false);
    }

    public List<Movie> getMovies(List<String> movieIds, boolean imdb) throws MovieDbException, IOException {
        if (!isInit) init();
        List<Movie> returnList = new ArrayList<Movie>();

        System.out.println("Obtaining Movie data from TMDB...");
        for (String idStr : movieIds) {
            MovieDb movieDb;
            if (imdb) {
                movieDb = api.getMovieInfoImdb(idStr, "EN");
            } else {
                movieDb = api.getMovieInfo(Integer.parseInt(idStr), "EN");
            }

            Movie m = new Movie();
            m.setId(movieDb.getId());
            m.setTitle(movieDb.getTitle());
            m.setYear(Strings.isNullOrEmpty(movieDb.getReleaseDate()) ? null : movieDb.getReleaseDate().substring(0, 4));
            m.setStatus(Strings.isNullOrEmpty(movieDb.getStatus()) ? null : movieDb.getStatus());
            m.setBudget(movieDb.getBudget());
            m.setRuntime(movieDb.getRuntime());
            m.setPopularity(movieDb.getPopularity());
            m.setPosterPath(Strings.isNullOrEmpty(movieDb.getPosterPath()) ? ImageHelper.NO_IMG : movieDb.getPosterPath());
            m.setImdb_id(movieDb.getImdbID());
            returnList.add(m);
        }
        System.out.println("Obtaining Movie data complete.");
        return returnList;
    }

    public List<Integer> getMovieCast(Integer movieId) throws MovieDbException {
        List<Integer> cast = new ArrayList<Integer>();

        TmdbResultsList<com.omertron.themoviedbapi.model.Person> tmdbCast = api.getMovieCasts(movieId);
        List<com.omertron.themoviedbapi.model.Person> castList = tmdbCast.getResults();
        for (com.omertron.themoviedbapi.model.Person p : castList) {
            if (p.getPersonType().equals(PersonType.CAST) && !cast.contains(p.getId()))
                cast.add(p.getId());
        }

        return cast;
    }


    public static void main(String[] args) {
        TMDBManager manager = new TMDBManager("7fa8a73aff547477888c3c5e03f864b1");
        MovieDb movie = null;
        try {
            manager.init();
            movie = manager.api.getMovieInfoImdb("tt2446042", "EN", "false");
            MovieDb z = manager.api.getMovieInfo(movie.getId(), "EN");
            com.omertron.themoviedbapi.model.Person p = manager.api.getPersonInfo(38673);
            System.out.println(z);
        } catch (MovieDbException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(movie.getTitle());
    }

    public void shutdown() {
        /* nothing to do? */
    }
}
