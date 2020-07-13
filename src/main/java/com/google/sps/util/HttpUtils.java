package com.google.sps.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.model.queue.QueueResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpUtils {
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * Sets up return body and response status when an invalid input comes in on POST
     * requests
     * @param errorCode: HTTP error code (e.g. 200, 404); generally a constant within HttpServletResponse
     * @param httpResponse: response to add status to
     * @param newResponse: response body
     * @throws IOException : if writer can't write (worse case scenario)
     */
    public static void sendInvalidPostResponse(int errorCode,
                                               HttpServletResponse httpResponse,
                                               QueueResponse newResponse) throws IOException {
        httpResponse.setStatus(errorCode);
        newResponse.setSuccess(false);
        newResponse.setEntity(null);
        httpResponse.getWriter().println(gson.toJsonTree(newResponse));
    }

    /**
     * Sets status to 400 if we can't parse input
     * @param response: response to add status to
     */
    public static void setInvalidGetResponse(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Sets status to 401 if not logged in
     * @param response: response to add status to
     */
    public static void sendNotLoggedIn(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
