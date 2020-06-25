package com.google.sps.servlets.movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.sps.KeyConfig;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MovieSearchServletTest extends Mockito {

    private ObjectMapper mapper = new ObjectMapper();
    private TmdbSearch movieSearchEngine = new TmdbSearch(new TmdbApi(KeyConfig.MOVIE_KEY));

    @Test
    public void testNormalSearch() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        MovieResultsPage confirmedResults = movieSearchEngine.searchMovie("benchwarmers", 1, null, false, 1);

        when(request.getParameter("query")).thenReturn("benchwarmers");
        when(request.getParameter("pageNumber")).thenReturn("1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new MovieSearchServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("query");
        verify(request, atLeast(1)).getParameter("pageNumber");
        writer.flush();

        MovieResultsPage result = mapper.readValue(stringWriter.toString(), MovieResultsPage.class);
        assertEquals(confirmedResults.getResults(), (result.getResults()));
    }

    @Test
    public void testBadMovieQuery() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("query")).thenReturn(null);
        when(request.getParameter("pageNumber")).thenReturn("1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new MovieSearchServlet().doGet(request, response);

        verify(request, atLeast(1)).getParameter("query");
        verify(response, times(1)).sendError(400);
    }

    @Test
    public void testBadMoviePage() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("query")).thenReturn(null);
        when(request.getParameter("pageNumber")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new MovieSearchServlet().doGet(request, response);

        verify(request, atLeast(1)).getParameter("query");
        verify(response, times(1)).sendError(400);
    }
}
