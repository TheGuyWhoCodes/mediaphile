package com.google.sps.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.servlets.book.BookDetailsServlet;
import com.google.sps.servlets.movie.MovieDetailsServlet;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {
    public static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch(Exception e) {
            return null;
        }
    }

    // Leverages servlets to check if an item of type contentType exists with ID == contentId
    // Returns null if parameters are invalid
    public static Boolean mediaItemExists(String contentType, String contentId) throws IOException {
        if (contentId.equals("")) {
            return null;
        }

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameter("id")).thenReturn(contentId);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(response.getWriter()).thenReturn(writer);

        try {
            switch (contentType) {
                case "book":
                    new BookDetailsServlet().doGet(request, response);
                    return true;
                case "movie":
                    new MovieDetailsServlet().doGet(request, response);
                    return true;
                default:
                    return null;
            }
        }
        catch (Exception e) {
            return false;
        }
    }
}
