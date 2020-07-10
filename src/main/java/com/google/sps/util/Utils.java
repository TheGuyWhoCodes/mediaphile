package com.google.sps.util;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.google.sps.model.queue.WantToWatchQueueObject.typeQueue;
import static com.google.sps.model.queue.WatchedQueueObject.typeViewed;

import com.google.sps.servlets.book.BookDetailsServlet;
import com.google.sps.servlets.movie.MovieDetailsServlet;

import java.util.Arrays;
import java.util.List;

public class Utils {
    public static class ContentType {
        public static final String BOOK = "book";
        public static final String MOVIE = "movie";
        private static final List<String> types = Arrays.asList(BOOK, MOVIE);

        private ContentType() {}

        public static boolean isType(String s) { return types.contains(s); }
    }

    public static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Helper function to parse out body data from requests
     * @param request: the request to parse out from
     * @return: Stringified JSON request
     * @throws IOException : if the body can't be parsed
     */
    public static String collectRequestLines(HttpServletRequest request) throws IOException {
        return request.getReader().lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);
    }

    /**
     * helper function to check if the type is valid
     * @param queueType: type to check
     * @return: true / false if the type is valid
     */
    public static boolean isCorrectListType(String queueType) {
        return queueType.equals(typeQueue) || queueType.equals(typeViewed);
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
                case ContentType.BOOK:
                    new BookDetailsServlet().getDetails(contentId);
                    return true;
                case ContentType.MOVIE:
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
