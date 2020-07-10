package com.google.sps.servlets.book;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;
import com.google.gson.Gson;
import com.google.sps.KeyConfig;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

@WebServlet("/books/details")
public class BookDetailsServlet extends HttpServlet {

    private static final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    private Gson gson = new Gson();

    public Volume getDetails(String id) throws GeneralSecurityException, IOException {
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

        return books.volumes().get(id).set("country", "US").execute();
    }

    /**
     * doGet() returns details of the particular volume with the given id
     * Returns error 400 if no id is provided
     * Returns error 404 if no book is returned by the API
     * Returns error 500 if HTTP connection fails
     * @param request: expects id parameter
     * @param response: returns a Volume object
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        String id = request.getParameter("id");
        if (id == null || id.equals("")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Volume volume = getDetails(id);
            response.getWriter().println(gson.toJson(volume));
        }
        catch (GeneralSecurityException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        catch (IOException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
