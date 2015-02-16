package com.scheidt.quickflix.data;

import com.scheidt.quickflix.models.Person;

import java.util.HashMap;

/**
 * Created by NewRob on 2/7/2015.
 */
public class PersonMap extends HashMap<Integer, Person>  {

    public static final String FILENAME = "person_data.qfx";


    public boolean isPersonPresent(int id) {
        return containsKey(id) ? true : false;
    }
}
