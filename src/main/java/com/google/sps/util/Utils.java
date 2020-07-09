package com.google.sps.util;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.google.sps.model.queue.WantToWatchQueueObject.typeQueue;
import static com.google.sps.model.queue.WatchedQueueObject.typeViewed;

public class Utils {

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
}
