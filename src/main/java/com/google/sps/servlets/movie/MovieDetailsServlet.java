package com.google.sps.servlets.movie;

import com.google.gson.Gson;
import com.google.sps.KeyConfig;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author chris
 * date: 6/21/2020
 */
@WebServlet("/movies/details")
public class MovieDetailsServlet extends HttpServlet {

    private TmdbMovies moviesQuery = new TmdbMovies(new TmdbApi(KeyConfig.MOVIE_KEY));
    private Gson gson = new Gson();

    /**
     * the doGet() for this servlet will return the user movie details provided a movie id and get
     * movie meta data, if no id is present in the request, the servlet will return a 400. if the query
     * can't find the movie, it'll return a 500.
     * @param request: needs a movie id query parameter
     * @param response: returns a MovieDb object, including movie metadata.
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        Integer id = null;

        if(null != request.getParameter("id")) {
            id = parseInt(request.getParameter("id"));
            if(null == id) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Uses null to default language to en-US
        MovieDb queryResponse = moviesQuery.getMovie(id, null);

        response.getWriter().println(gson.toJsonTree(queryResponse));
    }

    /**
     * parseInt method is used to parse an integer from a string (from a url param)
     * @param urlParam: the url param to convert from string -> integer
     * @return either an integer if the string is a valid integer, or null if it can't be parsed
     */
    private Integer parseInt(String urlParam) {
        try {
            return Integer.parseInt(urlParam);
        } catch(Exception e) {
            return null;
        }
    }
}
