package com.scheidt.quickflix.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.scheidt.quickflix.data.PersonMap;
import com.scheidt.quickflix.models.Person;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NewRob on 2/7/2015.
 */
public class StreamUtil {

    private static final String DATA_DIR = "data/";

    public static boolean saveFile(Object o, String filename) {

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        boolean success = false;

        try {
            fos = new FileOutputStream(DATA_DIR + filename);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
                success = false;
            }
        }
        return success;
    }

    public static Object readFile(String filename) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        Object o = null;

        try {
            fis = new FileInputStream(DATA_DIR + filename);
            ois = new ObjectInputStream(fis);

            try {
                o = ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                ois.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return o;
    }


    public static Object readFromGame(String filename) throws IOException, ClassNotFoundException {
        //FileHandle file = Gdx.files.local(DATA_DIR + filename);
        FileHandle file = Gdx.files.internal(DATA_DIR + filename);
        InputStream is = file.read();
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readObject();
    }

//
//
//    public static void writeEncode(Object o, String filename) throws IOException {
//
//        System.out.println(Gdx.files.isLocalStorageAvailable());
//        FileHandle file = Gdx.files.local(DATA_DIR + filename);
//        file.writeBytes(serialize(o), false);
//
//        String json = new Json().toJson(o);
//        System.out.println(json);
//    }
//
//
//    public static void readDecode(String filename) {
//        FileHandle file = Gdx.files.internal(DATA_DIR + filename);
//        byte[] bytes = file.readBytes();
//
//    }
//
//
//    public static byte[] serialize(Object obj) throws IOException {
//        ByteArrayOutputStream b = new ByteArrayOutputStream();
//        ObjectOutputStream o = new ObjectOutputStream(b);
//        o.writeObject(obj);
//        return b.toByteArray();
//    }
//
//    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
//        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
//        ObjectInputStream o = new ObjectInputStream(b);
//        return o.readObject();
//    }


}
