package com.google.sps.servlets.movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.sps.KeyConfig;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MovieDetailsServletTest extends Mockito {

    private ObjectMapper mapper = new ObjectMapper();
    private TmdbMovies moviesQuery = new TmdbMovies(new TmdbApi(KeyConfig.MOVIE_KEY));

    /**
     * Tests to see if a proper response comes out from proper movie id
     * @throws Exception
     */
    @Test
    public void testGoodMovieID() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("id")).thenReturn("132");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new MovieDetailsServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("id");
        writer.flush();
        MovieDb result = mapper.readValue(stringWriter.toString(), MovieDb.class);
        assertEquals(moviesQuery.getMovie(132, null), (result));
    }

    /**
     * Tests to see if a 400 is thrown if movie id is empty in query
     * @throws Exception
     */
    @Test
    public void testBadMovieId() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("id")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new MovieDetailsServlet().doGet(request, response);

        verify(request, atLeast(1)).getParameter("id");
        verify(response, times(1)).sendError(400);
    }

    /**
     * Tests to see if a 400 is thrown if movie id is empty in query
     * @throws Exception
     */
    @Test
    public void testInvalidId() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("id")).thenReturn("not a number");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new MovieDetailsServlet().doGet(request, response);

        verify(request, atLeast(1)).getParameter("id");
        verify(response, times(1)).sendError(400);
    }
}
