package com.scheidt.quickflix.batch;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.omertron.themoviedbapi.MovieDbException;
import com.scheidt.quickflix.data.Credit;
import com.scheidt.quickflix.data.CrossReference;
import com.scheidt.quickflix.data.MovieMap;
import com.scheidt.quickflix.data.PersonMap;
import com.scheidt.quickflix.models.Movie;
import com.scheidt.quickflix.models.Person;
import com.scheidt.quickflix.util.StreamUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * Created by NewRob on 2/8/2015.
 */
public class MainOperations {

    private static final String PEOPLE_DATA = "data/actorIDs.txt";
    private static final String MOVIE_DATA = "data/movieIds.txt";
    private static final String IMDB_MOVIE_DATA = "data/imdbMovieIds.txt";

    public static final ImmutableSet<String> ARGS = ImmutableSet.of(
            "PROCESS_PEOPLE",
            "PROCESS_MOVIES",
            "PEOPLE_IMG",
            "MOVIE_IMG",
            "TEST");

    private static final Dimension IMG_SIZE = new Dimension();

    TMDBManager tmdb;
    MySQLManager mysql;


    protected void init(String user, String pwd, String apikey) {
        tmdb = new TMDBManager(apikey);
        mysql = new MySQLManager(user, pwd);
    }

    public void processPeople() throws IOException, SQLException, MovieDbException {
        System.out.println("Start PROCESS_PEOPLE...");

        /* get txt file with actorIds */
        List<String> personIDs = Files.readLines(new File(PEOPLE_DATA), Charset.defaultCharset());
        System.out.println("Number of Person IDs in file: " + personIDs.size());

        /* get MySQL ids we've already gotten person data for */
        List<Integer> dbPersonIds = mysql.getPersonIds();
        System.out.println("Number of Person IDs in Person table: " + dbPersonIds.size());

        /* create new list for only new ids */
        List<Integer> newPersonIds = new ArrayList<Integer>();
        for (String id : personIDs) {
            if (!dbPersonIds.contains(Integer.parseInt(id))) newPersonIds.add(Integer.parseInt(id));
        }
        System.out.println("Number of new Person IDs that will be processed: " + newPersonIds.size());
        if (newPersonIds.size() > 0) {
             /* get Person data from TMDB */
            List<Person> personList = tmdb.getPeople(newPersonIds);

            /* add people and filmographies to mysql table */
            mysql.addPeople(personList);

            /* query credits table and find all movies that have counts greater than 2 */
            List<Integer> machedMovies = mysql.getMatchedMovieCredits();

            /* get MySQL for movies we've already gotten Movie data for */
            List<Integer> dbMovies = mysql.getMovieIds();

            /* query movie table and create new list for only new movies */
            List<Integer> newMovieIds = new ArrayList<Integer>();
            for (Integer id : machedMovies) {
                if (!dbMovies.contains(id)) newMovieIds.add(id);
            }
            System.out.println("Number of new Movie IDs that will be processed: " + newMovieIds.size());
            if (newMovieIds.size() > 0) {

                /* obtain movie data for new movies from TMDB */
                List<Movie> movieList = tmdb.getMovies(newMovieIds);

                /* add movies to mysql movies table */
                mysql.addMovies(movieList);
            }
        }
        System.out.println("PROCESS complete.");
    }

    public void processMovies() throws IOException, SQLException, MovieDbException {
        System.out.println("Start PROCESS_MOVIES...");

        /* get txt file with movieIds */
        Set<String> movieIds = new HashSet<String>(Files.readLines(new File(IMDB_MOVIE_DATA), Charset.defaultCharset()));
        Iterator<String> iter = movieIds.iterator();
        while (iter.hasNext()) {
            String str = iter.next();
            if (Strings.isNullOrEmpty(str) || str.startsWith("#")) iter.remove();
        }
        System.out.println("Number of Movie IDs in file: " + movieIds.size());

        /* get MySQL ids we've already gotten person data for */
        List<String> dbMovies = mysql.getIMDBIds();
        System.out.println("Number of Movie IDs in movie table: " + dbMovies.size());

        /* create new list for only new ids */
        List<String> newMovieIds = new ArrayList<String>();
        for (String id : movieIds) {
            if (!dbMovies.contains(id)) newMovieIds.add(id);
        }
        System.out.println("Number of new movie IDs that will be processed: " + newMovieIds.size());
        if (newMovieIds.size() > 0) {
             /* get movie data from TMDB */
            List<Movie> movieList = tmdb.getMovies(newMovieIds, true);

            /* add movies to mysql table */
            mysql.addMovies(movieList);

            /* get cast for these movies */
            for (Movie m : movieList) {
                List<Integer> cast = tmdb.getMovieCast(m.getId());
                mysql.addCastForMovie(m.getId(), cast);
            }

            /* query credits table and find all movies that have counts greater than x */
            List<Integer> matchedPeople = mysql.getMatchedPeopleCredits();

            /* get MySQL for people we've already gotten data for */
            List<Integer> dbPeople = mysql.getPersonIds();

            /* query movie table and create new list for only new movies */
            List<Integer> newPersonIds = new ArrayList<Integer>();
            for (Integer id : matchedPeople) {
                if (!dbPeople.contains(id)) newPersonIds.add(id);
            }
            System.out.println("Number of new people that will be added/processed: " + newPersonIds.size());
            if (newPersonIds.size() > 0) {

                /* obtain person data for new movies from TMDB */
                List<Person> personList = tmdb.getPeople(newPersonIds);

                /* add people to mysql person table */
                mysql.addPeople(personList);
            }
        }
        System.out.println("PROCESS_MOVIES complete.");
    }

    private void dataFile() throws SQLException {
        System.out.println("Start DATA_FILE...");

        PersonMap personMap = new PersonMap();
        CrossReference crs_ref = new CrossReference();
        MovieMap movieMap = new MovieMap();

        /*  Start with movies. Get movies from DB and create MovieMap */
        List<Movie> dbMovies = mysql.getMovies();
        System.out.println("Number of Movies from db: " + dbMovies.size());
        for (Movie m : dbMovies) {
            movieMap.put(m.getId(), m);
        }

        List<Person> dbPeople = mysql.getPeople();
        System.out.println("Number of People from db: " + dbPeople.size());
        for (Person p : dbPeople) {
            personMap.put(p.getId(), p);
        }


        List<Credit> credits = mysql.getMovieCredits();
        for (Credit c : credits) {
            Person p = personMap.get(c.getPersonId());
            Movie m = movieMap.get(c.getMovieId());
            if (p != null && m != null) {
                crs_ref.addRelationship(p, m);
            } else {
                // This is normal for where we have people with unmatched movie credits
            }
        }
        System.out.println("Total people added to cross reference table: " + crs_ref.getPersonMapSize());
        System.out.println("Total movies added to cross reference table: " + crs_ref.getMovieMapSize());

        /* save files */
        //StreamUtil.saveFile(personSet, PersonMap.PEOPLE_DATA);
        //StreamUtil.saveFile(movieSet, MovieMap.PEOPLE_DATA);
        StreamUtil.saveFile(crs_ref, "crs_ref.qfx");

        System.out.println("DATA_FILE complete.");
    }


    private void downloadPersonImages(String imgUrl) {
        CrossReference crs_ref = (CrossReference) StreamUtil.readFile(CrossReference.FILENAME);
        Set<Person> people = crs_ref.getAllPeople();
        System.out.println("Total people in crs_ref: " + people.size());
        URL url = null;
        int counter = 0;
        for (Person p : people) {
            try {
                if (p.getImgPath().equals(ImageHelper.NO_IMG)) continue; // no image available.
                File outputfile = new File("img/p" + p.getImgPath());
                if (outputfile.isFile()) continue;  // already downloaded
                url = new URL(imgUrl + p.getImgPath());
                BufferedImage image = ImageHelper.getImage(url);
                ImageIO.write(image, "jpg", outputfile);
                counter++;
            } catch (IOException e) {
                System.out.println("Could not obtain image for " + p.getName() + ". URL: " + url.getFile());
                e.printStackTrace();
            }
        }

        System.out.println("New Images downloaded: " + counter);
    }



    /**
     * Method to download images that haven't been obtained yet.
     * @param imgUrl
     */
    private void downloadMovieImages(String imgUrl) {
        CrossReference crs_ref = (CrossReference) StreamUtil.readFile(CrossReference.FILENAME);
        Set<Movie> movies = crs_ref.getAllMovies();
        for (Movie m : movies) {
            try {
                if (m.getPosterPath().equals(ImageHelper.NO_IMG)) continue; // no image available.

                File outputfile = new File("img/m" + m.getPosterPath());
                if (outputfile.isFile()) continue;  // already downloaded

                BufferedImage image = ImageHelper.getImage(new URL(imgUrl + m.getPosterPath()));
                ImageIO.write(image, "jpg", outputfile);
            } catch (IOException e) {
                System.out.println("Could not obtain image for " + m.getTitle() + ". URL: " + m.getPosterPath());
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to run tests to validate loading of data
     */
    private void runTests() {
        CrossReference crs_ref = (CrossReference) StreamUtil.readFile(CrossReference.FILENAME);
        assert crs_ref != null;
        assert crs_ref instanceof CrossReference;
        assert crs_ref.getMovieMapSize() > 0;
        assert crs_ref.getPersonMapSize() > 0;
        System.out.println("All tests completed successfully.");
    }



    /**
     * Main Program Entry for running TMDB, and MySQL operations.
     * @param args
     */
    public static void main(String[] args) {

        if (args.length < 4) {
            System.out.println("Invalid arguments. Must be at least 4:");
            System.out.println("  [0]: db username");
            System.out.println("  [1]: db password");
            System.out.println("  [2]: api key");
            System.out.print("  [3+]: an operation -");
            for (String s : ARGS) {
                System.out.print(" " + s);
            }
            System.exit(-1);
        }

        MainOperations ops = new MainOperations();
        ops.init(args[0], args[1], args[2]);

        for (int i = 3; i < args.length; i++) {
            if (args[i].equals("PROCESS_PEOPLE")) {
                try {
                    ops.processPeople();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (MovieDbException e) {
                    e.printStackTrace();
                }
            }

            if (args[i].equals("PROCESS_MOVIES")) {
                try {
                    ops.processMovies();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (MovieDbException e) {
                    e.printStackTrace();
                }
            }

            if (args[i].equals("DATA_FILE")) {
                try {
                    ops.dataFile();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (args[i].equals("TEST")) {
                ops.runTests();
            }

            if (args[i].equals("PEOPLE_IMG")) {
                ops.downloadPersonImages(TMDBManager.IMG_URL_LG);
            }

            if (args[i].equals("MOVIE_IMG")) {
                ops.downloadMovieImages(TMDBManager.IMG_URL_LG);
            }

        }

        ops.shutdown();

        System.out.println("Operations completed.");
    }




    private void shutdown() {
        tmdb.shutdown();
        mysql.shutdown();
    }
}
