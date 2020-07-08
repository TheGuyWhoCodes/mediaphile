package com.google.sps.servlets.review;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.ContextListener;
import com.google.sps.model.review.ReviewObject;
import com.google.sps.model.user.UserObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class ReviewServletTest extends Mockito {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private HttpServletResponse response;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeClass
    public static void initialize() {
        new ContextListener().initDbObjects();
    }

    @Before
    public void before() throws IOException {
        response = mock(HttpServletResponse.class);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @After
    public void tearDown() {
        helper.tearDown();
        ofy().clear();
    }

    @Test
    public void testGetNullParameters() throws IOException {
        helper.setEnvIsLoggedIn(false)
                .setUp();

        HttpServletRequest request = mock(HttpServletRequest.class);

        new ReviewServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetEmptyUserId() throws IOException {
        helper.setEnvIsLoggedIn(false)
                .setUp();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("userId")).thenReturn("");

        new ReviewServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetEmptyContent() throws IOException {
        helper.setEnvIsLoggedIn(false)
                .setUp();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn("");
        when(request.getParameter("contentId")).thenReturn("");

        new ReviewServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetEmptyBookId() throws IOException {
        helper.setEnvIsLoggedIn(false)
                .setUp();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn("book");
        when(request.getParameter("contentId")).thenReturn("");

        new ReviewServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetEmptyMovieId() throws IOException {
        helper.setEnvIsLoggedIn(false)
                .setUp();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn("movie");
        when(request.getParameter("contentId")).thenReturn("");


        new ReviewServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetGoodBook() throws IOException {
        helper.setEnvIsLoggedIn(false)
                .setUp();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn("book");
        when(request.getParameter("contentId")).thenReturn("ASImDQAAQBAJ");

        new ReviewServlet().doGet(request, response);
        writer.flush();

        assertEquals(stringWriter.toString().trim(), "[]");
    }

    @Test
    public void testGetGoodMovie() throws IOException {
        helper.setEnvIsLoggedIn(false)
                .setUp();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn("movie");
        when(request.getParameter("contentId")).thenReturn("127");

        new ReviewServlet().doGet(request, response);
        writer.flush();

        assertEquals(stringWriter.toString().trim(), "[]");
    }

    @Test
    public void testPostUnauthenticated() throws IOException {
        helper.setEnvIsLoggedIn(false)
                .setUp();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn("movie");
        when(request.getParameter("contentId")).thenReturn("127");
        when(request.getParameter("reviewTitle")).thenReturn("Test review");
        when(request.getParameter("reviewBody")).thenReturn("This is a test review");
        when(request.getParameter("rating")).thenReturn("3");

        new ReviewServlet().doPost(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testPostNullParameters() throws IOException {
        Map<String, Object> attr = new HashMap<>();
        String id = "123";
        attr.put("com.google.appengine.api.users.UserService.user_id_key", id);
        helper.setEnvEmail("test@example.com")
                .setEnvAttributes(attr)
                .setEnvAuthDomain("example.com")
                .setEnvIsLoggedIn(true)
                .setUp();
        ofy().save().entity(new UserObject(id, "test", "test@example.com", "")).now();

        HttpServletRequest request = mock(HttpServletRequest.class);

        new ReviewServlet().doPost(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testPostEmptyParameters() throws IOException {
        Map<String, Object> attr = new HashMap<>();
        String id = "123";
        attr.put("com.google.appengine.api.users.UserService.user_id_key", id);
        helper.setEnvEmail("test@example.com")
                .setEnvAttributes(attr)
                .setEnvAuthDomain("example.com")
                .setEnvIsLoggedIn(true)
                .setUp();
        ofy().save().entity(new UserObject(id, "test", "test@example.com", "")).now();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn("");
        when(request.getParameter("contentId")).thenReturn("");
        when(request.getParameter("reviewTitle")).thenReturn("");
        when(request.getParameter("reviewBody")).thenReturn("");
        when(request.getParameter("rating")).thenReturn("");

        new ReviewServlet().doPost(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testPostBadRating() throws IOException {
        Map<String, Object> attr = new HashMap<>();
        String id = "123";
        attr.put("com.google.appengine.api.users.UserService.user_id_key", id);
        helper.setEnvEmail("test@example.com")
                .setEnvAttributes(attr)
                .setEnvAuthDomain("example.com")
                .setEnvIsLoggedIn(true)
                .setUp();
        ofy().save().entity(new UserObject(id, "test", "test@example.com", "")).now();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn("movie");
        when(request.getParameter("contentId")).thenReturn("127");
        when(request.getParameter("reviewTitle")).thenReturn("Test review");
        when(request.getParameter("reviewBody")).thenReturn("This is a test review");
        when(request.getParameter("rating")).thenReturn("7");

        new ReviewServlet().doPost(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testPostGoodReview() throws IOException {
        Map<String, Object> attr = new HashMap<>();
        String id = "123";
        attr.put("com.google.appengine.api.users.UserService.user_id_key", id);
        helper.setEnvEmail("test@example.com")
                .setEnvAttributes(attr)
                .setEnvAuthDomain("example.com")
                .setEnvIsLoggedIn(true)
                .setUp();
        ofy().save().entity(new UserObject(id, "test", "test@example.com", "")).now();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn("movie");
        when(request.getParameter("contentId")).thenReturn("127");
        when(request.getParameter("reviewTitle")).thenReturn("Test review");
        when(request.getParameter("reviewBody")).thenReturn("This is a test review");
        when(request.getParameter("rating")).thenReturn("3");

        new ReviewServlet().doPost(request, response);
        writer.flush();

        List<ReviewObject> reviews = ofy().load().type(ReviewObject.class)
                .filter("authorId", "123")
                .list();
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());

        reviews = ofy().load().type(ReviewObject.class)
                .filter("contentType", "movie")
                .filter("contentId", "127")
                .list();
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
    }
}