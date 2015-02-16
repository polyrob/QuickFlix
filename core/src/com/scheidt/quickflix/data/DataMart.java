package com.scheidt.quickflix.data;

import com.scheidt.quickflix.util.StreamUtil;

/**
 * Created by NewRob on 2/7/2015.
 */
public class DataMart {

    private static DataMart instance = null;

    private static PersonMap personMap;
    private static MovieMap movieMap;

    public static DataMart getInstance() {
        if (instance == null) {
            instance = new DataMart();
        }
        return instance;
    }


    /* Singleton private constructor */
    private DataMart() {
    }

    public static void loadData() {

        personMap = (PersonMap) StreamUtil.readFile(PersonMap.FILENAME);
        movieMap = (MovieMap) StreamUtil.readFile(MovieMap.FILENAME);

    }


}
