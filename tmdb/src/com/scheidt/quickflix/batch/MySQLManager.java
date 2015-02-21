package com.scheidt.quickflix.batch;

import com.scheidt.quickflix.data.Credit;
import com.scheidt.quickflix.models.Movie;
import com.scheidt.quickflix.models.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by NewRob on 2/8/2015.
 */
public class MySQLManager {

    private static final String MIN_COUNT = "5";

    private static final String URL = "jdbc:mysql://localhost:3306/quickflix";

    private String user;
    private String pwd;
    private boolean isInit;

    Connection con = null;
    Statement st = null;
    ResultSet rs = null;

    private static final String GET_PERSON_IDS_QUERY = "SELECT person_id FROM person";
    private static final String GET_MOVIE_IDS_QUERY = "SELECT movie_id FROM movie";
    private static final String GET_IMDB_IDS_QUERY = "SELECT imdb_id FROM movie";
    private static final String ADD_PERSON_QUERY = "INSERT INTO person (person_id, name, birth_year, popularity, img_path, imdb_id) VALUES ";
    private static final String ADD_CREDITS_QUERY = "INSERT INTO credits (person_id, movie_id) VALUES ";
    private static final String ADD_MOVIES_QUERY = "INSERT INTO movie (movie_id, title, year, status, budget, runtime, popularity, img_path, imdb_id) VALUES ";
    private static final String MATCHED_MOVIES_QUERY = "SELECT distinct movie_id FROM credits WHERE movie_id in ( SELECT movie_id FROM credits GROUP BY movie_id HAVING count(*) > "+MIN_COUNT+")";
    private static final String MATCHED_PEOPLE_QUERY = "SELECT distinct person_id FROM credits WHERE person_id in ( SELECT person_id FROM credits GROUP BY person_id HAVING count(*) > "+MIN_COUNT+")";

    private static final String GET_MOVIES_QUERY = "SELECT movie_id, title, year, status, budget, runtime, popularity, img_path, imdb_id FROM movie";
    private static final String GET_CREDITS = "SELECT person_id, movie_id, rank FROM credits";
    private static final String GET_PEOPLE = "SELECT person_id, name, birth_year, popularity, img_path, imdb_id FROM person";

    public MySQLManager(String user, String pwd) {
        this.user = user;
        this.pwd = pwd;
    }


    public void init() throws SQLException {
        System.out.println("Initializing mysql connection...");
            con = DriverManager.getConnection(URL, user, pwd);
            st = con.createStatement();
            isInit = true;
            System.out.println("Success.");

    }


    public List<Integer> getPersonIds() throws SQLException {
        if (!isInit) init();

        List<Integer> dbIds = new ArrayList<Integer>();

        try {
            rs = st.executeQuery(GET_PERSON_IDS_QUERY);

            while (rs.next()) {
                dbIds.add(rs.getInt("person_id"));
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(MySQLManager.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(MySQLManager.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return dbIds;
    }

    public void addPeople(List<Person> personList) throws SQLException {
        if (!isInit) init();

        StringBuilder sb_person = new StringBuilder();
        //StringBuilder sb_credits = new StringBuilder();
        sb_person.append(ADD_PERSON_QUERY);
        //sb_credits.append(ADD_CREDITS_QUERY);

        for (Person p : personList) {
            sb_person.append("(");
            sb_person.append("'").append(p.getId()).append("'");
            sb_person.append(",");
            sb_person.append("'").append(p.getName().replaceAll("'", "''")).append("'");
            sb_person.append(",");
            sb_person.append("'").append(p.getBirthYear()).append("'");
            sb_person.append(",");
            sb_person.append("'").append(p.getPopularity()).append("'");
            sb_person.append(",");
            sb_person.append("'").append(p.getImgPath()).append("'");
            sb_person.append(",");
            sb_person.append("'").append(p.getImdbId()).append("'");
            sb_person.append("),");
//
//            for (Integer i : p.getMovies()) {
//                sb_credits.append("(");
//                sb_credits.append("'").append(p.getId()).append("'");
//                sb_credits.append(",");
//                sb_credits.append("'").append(i).append("'");
//                sb_credits.append("),");
//            }

        }
        sb_person.deleteCharAt(sb_person.length()-1); // remove last comma
//        sb_credits.deleteCharAt(sb_credits.length()-1); // remove last comma

        int updatePersonRsp = st.executeUpdate(sb_person.toString());
//        int updateCreditsRsp = st.executeUpdate(sb_credits.toString());
        System.out.println("addPeople() complete.");
    }


    public void addCredits(List<Credit> castList) throws SQLException {
        StringBuilder sb_credits = new StringBuilder();
        sb_credits.append(ADD_CREDITS_QUERY);

        for (Credit c : castList) {
            sb_credits.append("(");
            sb_credits.append("'").append(c.getPersonId()).append("'");
            sb_credits.append(",");
            sb_credits.append("'").append(c.getMovieId()).append("'");
            sb_credits.append(",");
            sb_credits.append("'").append(c.getRank()).append("'");
            sb_credits.append("),");
        }
        sb_credits.deleteCharAt(sb_credits.length()-1); // remove last comma
        int updateCreditsRsp = st.executeUpdate(sb_credits.toString());
    }


    public List<Integer> getMatchedMovieCredits() throws SQLException {
        if (!isInit) init();

        List<Integer> machedMovieIds = new ArrayList<Integer>();

        try {
            rs = st.executeQuery(MATCHED_MOVIES_QUERY);

            while (rs.next()) {
                machedMovieIds.add(rs.getInt("movie_id"));
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(MySQLManager.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(MySQLManager.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return machedMovieIds;
    }

    public List<Integer> getMatchedPeopleCredits() throws SQLException {
        if (!isInit) init();
        List<Integer> matchedPersonIds = new ArrayList<Integer>();
        try {
            rs = st.executeQuery(MATCHED_PEOPLE_QUERY);
            while (rs.next()) {
                matchedPersonIds.add(rs.getInt("person_id"));
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(MySQLManager.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(MySQLManager.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return matchedPersonIds;
    }


    public List<Integer> getMovieIds() throws SQLException {
        if (!isInit) init();
        List<Integer> dbIds = new ArrayList<Integer>();
        try {
            rs = st.executeQuery(GET_MOVIE_IDS_QUERY);
            while (rs.next()) {
                dbIds.add(rs.getInt("movie_id"));
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(MySQLManager.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(MySQLManager.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return dbIds;
    }


    public List<String> getIMDBIds() throws SQLException {
        if (!isInit) init();
        List<String> dbIds = new ArrayList<String>();
        try {
            rs = st.executeQuery(GET_IMDB_IDS_QUERY);
            while (rs.next()) {
                dbIds.add(rs.getString("imdb_id"));
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(MySQLManager.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(MySQLManager.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return dbIds;
    }

    public List<Movie> getMovies() throws SQLException {
        if (!isInit) init();

        List<Movie> movieList = new ArrayList<Movie>();

        try {
            rs = st.executeQuery(GET_MOVIES_QUERY);

            while (rs.next()) {
                Movie m = new Movie();
                m.setId(rs.getInt("movie_id"));
                m.setTitle(rs.getString("title"));
                m.setYear(rs.getString("year"));
                m.setStatus(rs.getString("status"));
                m.setBudget(rs.getInt("budget"));
                m.setRuntime(rs.getInt("runtime"));
                m.setPopularity(rs.getFloat("popularity"));
                m.setPosterPath(rs.getString("img_path"));
                m.setImdb_id(rs.getString("imdb_id"));
                movieList.add(m);
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(MySQLManager.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(MySQLManager.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return movieList;
    }


    public void addMovies(List<Movie> movieList) throws SQLException {
        if (!isInit) init();

        StringBuilder sb_movies = new StringBuilder();
        sb_movies.append(ADD_MOVIES_QUERY);

        for (Movie m : movieList) {
            sb_movies.append("(");
            sb_movies.append("'").append(m.getId()).append("'");
            sb_movies.append(",");
            sb_movies.append("'").append(m.getTitle().replaceAll("'", "''")).append("'");
            sb_movies.append(",");
            sb_movies.append("'").append(m.getYear()).append("'");
            sb_movies.append(",");
            sb_movies.append("'").append(m.getStatus()).append("'");
            sb_movies.append(",");
            sb_movies.append("'").append(m.getBudget()).append("'");
            sb_movies.append(",");
            sb_movies.append("'").append(m.getRuntime()).append("'");
            sb_movies.append(",");
            sb_movies.append("'").append(m.getPopularity()).append("'");
            sb_movies.append(",");
            sb_movies.append("'").append(m.getPosterPath()).append("'");
            sb_movies.append(",");
            sb_movies.append("'").append(m.getImdb_id()).append("'");
            sb_movies.append("),");
        }
        sb_movies.deleteCharAt(sb_movies.length()-1); // remove last comma

        int updateMovieRsp = st.executeUpdate(sb_movies.toString());
    }


    public List<Credit> getMovieCredits() throws SQLException {
        if (!isInit) init();
        List<Credit> creditsList = new ArrayList<Credit>();

        try {
            rs = st.executeQuery(GET_CREDITS);

            while (rs.next()) {
                Credit c = new Credit(rs.getInt("person_id"), rs.getInt("movie_id"), (rs.getInt("rank")));
                creditsList.add(c);
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(MySQLManager.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(MySQLManager.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return creditsList;
    }



    public List<Person> getPeople() throws SQLException {
        if (!isInit) init();
        List<Person> personList = new ArrayList<Person>();

        try {
            rs = st.executeQuery(GET_PEOPLE);

            while (rs.next()) {
                Person p = new Person();
                p.setId(rs.getInt("person_id"));
                p.setName(rs.getString("name"));
                p.setBirthYear(Integer.parseInt(rs.getString("birth_year")));
                p.setPopularity(rs.getFloat("popularity"));
                p.setImgPath(rs.getString("img_path"));
                p.setImdbId(rs.getString("imdb_id"));
                personList.add(p);
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(MySQLManager.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(MySQLManager.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return personList;
    }


    public void shutdown() {
        try {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (con != null) con.close();

            System.out.println("MySQL shutdown complete.");
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(MySQLManager.class.getName());
            lgr.log(Level.WARNING, ex.getMessage(), ex);
        }
        isInit = false;
    }
}

