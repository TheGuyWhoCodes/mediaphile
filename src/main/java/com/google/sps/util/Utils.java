package com.google.sps.util;

import com.google.sps.servlets.book.BookDetailsServlet;
import com.google.sps.servlets.movie.MovieDetailsServlet;

import java.io.*;

public class Utils {
    public static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch(Exception e) {
            return null;
        }
    }

    // Leverages servlets to check if an item of type contentType exists with ID == contentId
    // Returns null if parameters are invalid
    public static Boolean mediaItemExists(String contentType, String contentId) {
        if (contentId == null || contentId.isEmpty()
            || contentType == null || contentType.isEmpty()) {
            return null;
        }

        try {
            switch (contentType) {
                case "book":
                    new BookDetailsServlet().getDetails(contentId);
                    return true;
                case "movie":
                    Integer intId = parseInt(contentId);
                    if (intId == null) return null;
                    new MovieDetailsServlet().getDetails(intId);
                    return true;
                default:
                    return null;
            }
        }
        catch (IOException e) {
            return false;
        }
        catch (Exception e) {
            return null;
        }
    }
}
