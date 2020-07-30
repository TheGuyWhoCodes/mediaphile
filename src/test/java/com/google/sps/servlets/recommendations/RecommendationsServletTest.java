package com.google.sps.servlets.recommendations;

import com.google.sps.util.Utils.ContentType;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class RecommendationsServletTest extends Mockito {
    public static final String GOOD_BOOK_ID = "ASImDQAAQBAJ";
    public static final String GOOD_MOVIE_ID = "127";

    @Test
    public void testGetNullParameters() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new RecommendationsServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetEmptyParameters() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("mediaType")).thenReturn("");
        when(request.getParameter("mediaId")).thenReturn("");

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new RecommendationsServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetGoodBook() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("mediaType")).thenReturn(ContentType.BOOK);
        when(request.getParameter("mediaId")).thenReturn(GOOD_BOOK_ID);

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new RecommendationsServlet().doGet(request, response);
        writer.flush();

        assertFalse(writer.toString().trim().equals(""));
    }

    @Test
    public void testGetGoodMovie() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("mediaType")).thenReturn(ContentType.MOVIE);
        when(request.getParameter("mediaId")).thenReturn(GOOD_MOVIE_ID);

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new RecommendationsServlet().doGet(request, response);
        writer.flush();

        assertFalse(writer.toString().trim().equals(""));
    }

    @Test
    public void testGetSecondPage() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("mediaType")).thenReturn(ContentType.MOVIE);
        when(request.getParameter("mediaId")).thenReturn(GOOD_MOVIE_ID);

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new RecommendationsServlet().doGet(request, response);
        writer.flush();
        String firstPage = writer.toString();

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        when(request.getParameter("pageNumber")).thenReturn("1");
        new RecommendationsServlet().doGet(request, response);
        writer.flush();
        String secondPage = writer.toString();

        assertFalse(firstPage.trim().equals(secondPage.trim()));
    }
}
