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

    private static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private Gson gson = new Gson();

    /**
     * doGet() returns details of the particular volume with the given id
     * Returns error 400 if no id is provided
     * Returns error 500 if no movie is returned by the API
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

        final NetHttpTransport HTTP_TRANSPORT;
        try {
            // Can throw an exception if trusted certificate cannot be established
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        }
        catch (GeneralSecurityException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Books books = new Books.Builder(HTTP_TRANSPORT, JSON_FACTORY, null)
                .setApplicationName(KeyConfig.APPLICATION_NAME)
                .build();

        Volume volume = books.volumes().get(id).execute();

        response.getWriter().println(gson.toJsonTree(volume));
    }
}
