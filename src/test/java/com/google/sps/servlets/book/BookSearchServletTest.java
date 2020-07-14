package com.google.sps.servlets.book;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volumes;
import com.google.gson.Gson;
import com.google.sps.KeyConfig;
import org.json.simple.JSONObject;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BookSearchServletTest extends Mockito {

    private static final long RESULTS_PER_PAGE = 5L;
    public static final String GOOD_BOOK_QUERY = "War and Peace";

    private static JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private Gson gson = new Gson();

    @Test
    public void testNormalSearch() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("query")).thenReturn(GOOD_BOOK_QUERY);
        when(request.getParameter("pageNumber")).thenReturn("0");

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new BookSearchServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("query");
        verify(request, atLeast(1)).getParameter("pageNumber");
        writer.flush();

        verify(response, never()).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verify(response, never()).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testNormalSearchDetails() throws IOException, GeneralSecurityException {
        Volumes results = new BookSearchServlet().getResults(GOOD_BOOK_QUERY, 0);

        final NetHttpTransport httpTransport;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        }
        catch (Exception e) {
            fail("HTTP transport connection failed");
            return;
        }

        Books books = new Books.Builder(httpTransport, jsonFactory, null)
                .setApplicationName(KeyConfig.APPLICATION_NAME)
                .build();

        Volumes volumes = books.volumes().list(GOOD_BOOK_QUERY)
                .setMaxResults(RESULTS_PER_PAGE)
                .setStartIndex(0L)
                .set("country", "US")
                .execute();


        assertEquals(volumes.getItems().get(0).getId(), results.getItems().get(0).getId());
    }


    @Test
    public void testNullBookQuery() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("query")).thenReturn(null);
        when(request.getParameter("pageNumber")).thenReturn("0");

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new BookSearchServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("query");
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testEmptyBookQuery() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("query")).thenReturn("");
        when(request.getParameter("pageNumber")).thenReturn("0");

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new BookSearchServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("query");
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testNullBookPage() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("query")).thenReturn(GOOD_BOOK_QUERY);
        when(request.getParameter("pageNumber")).thenReturn(null);

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new BookSearchServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("pageNumber");
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testBadBookPage() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("query")).thenReturn(GOOD_BOOK_QUERY);
        when(request.getParameter("pageNumber")).thenReturn("abc");

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new BookSearchServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("pageNumber");
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
}
