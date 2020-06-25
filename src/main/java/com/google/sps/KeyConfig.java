package com.google.sps;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KeyConfig {
    private static final Properties properties = new Properties();

    static {
        try {
            InputStream input = KeyConfig.class.getClassLoader().getResourceAsStream("config.properties");
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String MOVIE_KEY = properties.getProperty("movie_apikey");

}
