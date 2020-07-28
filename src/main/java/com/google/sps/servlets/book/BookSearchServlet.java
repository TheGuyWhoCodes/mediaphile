package com.google.sps.servlets.book;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;
import com.google.gson.Gson;
import com.google.sps.KeyConfig;
import com.google.sps.model.results.ResultsObject;
import org.json.simple.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.sps.util.Utils;

import com.google.api.services.books.Books;

@WebServlet("/books/search")
public class BookSearchServlet extends HttpServlet {

    private static final long RESULTS_PER_PAGE = 20L;

    private static JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private Gson gson = new Gson();

    public Volumes getResults(String query, int pageNumber) throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport;
        try {
            // Can throw an exception if trusted certificate cannot be established
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        }
        catch (Exception e) {
            throw new GeneralSecurityException();
        }

        Books books = new Books.Builder(httpTransport, jsonFactory, null)
                .setApplicationName(KeyConfig.APPLICATION_NAME)
                .build();

        return books.volumes().list(query)
                .setMaxResults(RESULTS_PER_PAGE)
                .setStartIndex(pageNumber*RESULTS_PER_PAGE)
                .set("country", "US")
                .execute();
    }


    /**
     * doGet() handles search queries to Books database.
     * @param request: a request may have the following query params: query, pageNumber
     * @param response: a json object returning pagination info and results
     * @throws IOException:
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        String query = request.getParameter("query");
        Integer pageNumber = Utils.parseInt(request.getParameter("pageNumber"));
        if (query == null || query.equals("") || pageNumber == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Volumes volumes = getResults(query, pageNumber);
            response.getWriter().println(gson.toJsonTree(
                    new ResultsObject<>(volumes.getItems(),
                            volumes.getTotalItems(),
                            volumes.getTotalItems() / ((int) RESULTS_PER_PAGE),
                            pageNumber)));
        }
        catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
