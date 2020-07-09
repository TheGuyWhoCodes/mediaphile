package com.google.sps.servlets.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;
import com.google.gson.Gson;
import com.google.sps.KeyConfig;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BookDetailsServletTest extends Mockito {

    private static final String GOOD_BOOK_ID = "ASImDQAAQBAJ";
    private static final String BAD_BOOK_ID = "notRealBook";

    private static final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    @Test
    public void testNormalQuery() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("id")).thenReturn(GOOD_BOOK_ID);

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new BookDetailsServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("id");
        writer.flush();

        verify(response, never()).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verify(response, never()).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testNormalQueryDetails() throws IOException, GeneralSecurityException {
        Volume result = new BookDetailsServlet().getDetails(GOOD_BOOK_ID);

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

        Volume volume = books.volumes().get(GOOD_BOOK_ID).set("country", "US").execute();

        assertEquals(result.getId(), volume.getId());
    }

    @Test
    public void testNullId() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new BookDetailsServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("id");
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testEmptyId() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("id")).thenReturn("");

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new BookDetailsServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("id");
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testNoBookFound() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("id")).thenReturn(BAD_BOOK_ID);

        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new BookDetailsServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("id");
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
