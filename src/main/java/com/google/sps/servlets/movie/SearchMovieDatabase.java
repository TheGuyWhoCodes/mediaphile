package com.google.sps.servlets.movie;

import com.google.gson.Gson;
import com.google.sps.KeyConfig;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import org.json.simple.JSONObject;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chris
 * date: 6/19/2020
 */
@WebServlet("/movies/search")
public class SearchMovieDatabase extends HttpServlet {

    private TmdbSearch movieSearchEngine = new TmdbSearch(new TmdbApi(KeyConfig.MOVIE_KEY));
    private Gson gson = new Gson();
    private JSONObject json = new JSONObject();

    /**
     * doGet() handles search queries to tmdb database.
     * @param request: a request may have the following query params: query, pageNumber
     * @param response: a json object returning pagination info and results
     * @throws IOException:
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;");

        Integer pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
        String query = request.getParameter("query");

        // using 0 for the search year returns all years
        MovieResultsPage searchResults = movieSearchEngine.searchMovie(query, 0, null, false, pageNumber);

        json.put("results", gson.toJsonTree(searchResults.getResults()));
        json.put("totalResults", searchResults.getTotalResults());
        json.put("totalPages", searchResults.getTotalPages());
        json.put("page", searchResults.getPage());

        response.getWriter().println(json);
    }
}
