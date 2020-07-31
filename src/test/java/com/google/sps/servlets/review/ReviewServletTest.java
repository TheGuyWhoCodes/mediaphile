package com.google.sps.servlets.review;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.ContextListener;
import com.google.sps.model.review.ReviewObject;
import com.google.sps.model.user.UserObject;
import com.google.sps.servlets.TestDelegatingServletInputStream;
import com.google.sps.util.Utils.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class ReviewServletTest extends Mockito {

    public static final String GOOD_BOOK_ID = "ASImDQAAQBAJ";
    public static final String GOOD_MOVIE_ID = "127";
    public static final String BAD_BOOK_ID = "notRealBook";
    public static final String BAD_MOVIE_ID = "999999";
    public static final String DUMMY_REVIEW_TITLE = "Test review";
    public static final String DUMMY_REVIEW_BODY = "This is a test review";
    public static final String TOO_BIG_RATING = "7";
    public static final String GOOD_DUMMY_RATING = "3";

    public static final String DUMMY_MOVIE_TITLE = "test movie";
    public static final String DUMMY_MOVIE_ART_URL = "";

    public static final String DUMMY_BOOK_TITLE = "test book";
    public static final String DUMMY_BOOK_ART_URL = "";

    public static final String DUMMY_USER_ID = "123";
    public static final String DUMMY_EMAIL = "test@example.com";
    public static final String DUMMY_USERNAME = "test";
    public static final String DUMMY_DOMAIN = "example.com";
    public static final String DUMMY_PROFILE_PIC_URL = "";

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

    public void initLoggedOut() {
        helper.setEnvIsLoggedIn(false)
                .setUp();
    }

    public void initLoggedIn() {
        Map<String, Object> attr = new HashMap<>();
        attr.put("com.google.appengine.api.users.UserService.user_id_key", DUMMY_USER_ID);
        helper.setEnvEmail(DUMMY_EMAIL)
                .setEnvAttributes(attr)
                .setEnvAuthDomain(DUMMY_DOMAIN)
                .setEnvIsLoggedIn(true)
                .setUp();
        ofy().save().entity(new UserObject(DUMMY_USER_ID, DUMMY_USERNAME, DUMMY_EMAIL, DUMMY_PROFILE_PIC_URL)).now();
    }

    @Test
    public void testGetNullParameters() throws IOException {
        initLoggedOut();

        HttpServletRequest request = mock(HttpServletRequest.class);

        new ReviewServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetEmptyUserId() throws IOException {
        initLoggedOut();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("userId")).thenReturn("");

        new ReviewServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetEmptyContent() throws IOException {
        initLoggedOut();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn("");
        when(request.getParameter("contentId")).thenReturn("");

        new ReviewServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetEmptyBookId() throws IOException {
        initLoggedOut();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn(ContentType.BOOK);
        when(request.getParameter("contentId")).thenReturn("");

        new ReviewServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetEmptyMovieId() throws IOException {
        initLoggedOut();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn(ContentType.MOVIE);
        when(request.getParameter("contentId")).thenReturn("");


        new ReviewServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetGoodUser() throws IOException {
        initLoggedIn(); // To initialize user in Datastore

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("userId")).thenReturn(DUMMY_USER_ID);
        when(request.getParameter("pageNumber")).thenReturn("1");

        new ReviewServlet().doGet(request, response);
        writer.flush();

        assertEquals(stringWriter.toString().trim(), "[]");
    }

    @Test
    public void testGetGoodBook() throws IOException {
        initLoggedOut();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn(ContentType.BOOK);
        when(request.getParameter("contentId")).thenReturn(GOOD_BOOK_ID);
        when(request.getParameter("pageNumber")).thenReturn("1");

        new ReviewServlet().doGet(request, response);
        writer.flush();

        assertEquals(stringWriter.toString().trim(), "[]");
    }

    @Test
    public void testGetGoodMovie() throws IOException {
        initLoggedOut();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn(ContentType.BOOK);
        when(request.getParameter("contentId")).thenReturn(GOOD_BOOK_ID);
        when(request.getParameter("pageNumber")).thenReturn("1");

        new ReviewServlet().doGet(request, response);
        writer.flush();

        assertEquals(stringWriter.toString().trim(), "[]");
    }

    @Test
    public void testGetSpecificReview() throws IOException {
        initLoggedIn(); // To initialize user in Datastore

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("userId")).thenReturn(DUMMY_USER_ID);
        when(request.getParameter("contentType")).thenReturn(ContentType.BOOK);
        when(request.getParameter("contentId")).thenReturn(GOOD_BOOK_ID);

        UserObject userObject = ofy().load().type(UserObject.class).id(DUMMY_USER_ID).now();
        ReviewObject reviewObject = new ReviewObject(userObject,
                ContentType.BOOK, GOOD_BOOK_ID,
                DUMMY_BOOK_TITLE, DUMMY_BOOK_ART_URL,
                DUMMY_REVIEW_TITLE, DUMMY_REVIEW_BODY, Integer.parseInt(GOOD_DUMMY_RATING));
        ofy().save().entity(reviewObject).now();
        new ReviewServlet().doGet(request, response);
        writer.flush();

        assertFalse(stringWriter.toString().trim().equals("[]"));
        assertFalse(stringWriter.toString().trim().equals("[[]]"));
    }

    @Test
    public void testPostUnauthenticated() throws IOException {
        initLoggedOut();

        HttpServletRequest request = mock(HttpServletRequest.class);
        String json = "{\n" +
                "\t\"authorId\": \"" + DUMMY_USER_ID + "\",\n" +
                "\t\"authorName\": \"" + DUMMY_USERNAME + "\",\n" +
                "\t\"contentType\": \"" + ContentType.MOVIE + "\",\n" +
                "\t\"contentId\": \"" + GOOD_MOVIE_ID + "\",\n" +
                "\t\"contentTitle\": \"" + DUMMY_MOVIE_TITLE + "\",\n" +
                "\t\"artUrl\": \"" + DUMMY_MOVIE_ART_URL + "\",\n" +
                "\t\"reviewTitle\": \"" + DUMMY_REVIEW_TITLE + "\",\n" +
                "\t\"reviewBody\": \"" + DUMMY_REVIEW_TITLE+ "\",\n" +
                "\t\"rating\": \"" + GOOD_DUMMY_RATING + "\"\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        new ReviewServlet().doPost(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testPostNullParameters() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);

        new ReviewServlet().doPost(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testPostEmptyParameters() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);
        String json = "{\n" +
                "\t\"authorId\": \"" + "\",\n" +
                "\t\"authorName\": \"" + "\",\n" +
                "\t\"contentType\": \"" + "\",\n" +
                "\t\"contentId\": \"" + "\",\n" +
                "\t\"contentTitle\": \"" + "\",\n" +
                "\t\"artUrl\": \"" + "\",\n" +
                "\t\"reviewTitle\": \"" + "\",\n" +
                "\t\"reviewBody\": \"" + "\",\n" +
                "\t\"rating\": \"" + "\"\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        new ReviewServlet().doPost(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testPostBadRating() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);
        String json = "{\n" +
                "\t\"authorId\": \"" + DUMMY_USER_ID + "\",\n" +
                "\t\"authorName\": \"" + DUMMY_USERNAME + "\",\n" +
                "\t\"contentType\": \"" + ContentType.MOVIE + "\",\n" +
                "\t\"contentId\": \"" + GOOD_MOVIE_ID + "\",\n" +
                "\t\"contentTitle\": \"" + DUMMY_MOVIE_TITLE + "\",\n" +
                "\t\"artUrl\": \"" + DUMMY_MOVIE_ART_URL + "\",\n" +
                "\t\"reviewTitle\": \"" + DUMMY_REVIEW_TITLE + "\",\n" +
                "\t\"reviewBody\": \"" + DUMMY_REVIEW_TITLE+ "\",\n" +
                "\t\"rating\": \"" + TOO_BIG_RATING + "\"\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        new ReviewServlet().doPost(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testPostGoodMovieReview() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);
        String json = "{\n" +
                "\t\"authorId\": \"" + DUMMY_USER_ID + "\",\n" +
                "\t\"authorName\": \"" + DUMMY_USERNAME + "\",\n" +
                "\t\"contentType\": \"" + ContentType.MOVIE + "\",\n" +
                "\t\"contentId\": \"" + GOOD_MOVIE_ID + "\",\n" +
                "\t\"contentTitle\": \"" + DUMMY_MOVIE_TITLE + "\",\n" +
                "\t\"artUrl\": \"" + DUMMY_MOVIE_ART_URL + "\",\n" +
                "\t\"reviewTitle\": \"" + DUMMY_REVIEW_TITLE + "\",\n" +
                "\t\"reviewBody\": \"" + DUMMY_REVIEW_TITLE+ "\",\n" +
                "\t\"rating\": \"" + GOOD_DUMMY_RATING + "\"\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        new ReviewServlet().doPost(request, response);
        writer.flush();

        List<ReviewObject> reviews = ofy().load().type(ReviewObject.class)
                .filter("userId", DUMMY_USER_ID)
                .list();
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());

        reviews = ofy().load().type(ReviewObject.class)
                .filter("contentType", ContentType.MOVIE)
                .filter("contentId", GOOD_MOVIE_ID)
                .list();
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
    }

    @Test
    public void testPostGoodBookReview() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);
        String json = "{\n" +
                "\t\"authorId\": \"" + DUMMY_USER_ID + "\",\n" +
                "\t\"authorName\": \"" + DUMMY_USERNAME + "\",\n" +
                "\t\"contentType\": \"" + ContentType.BOOK + "\",\n" +
                "\t\"contentId\": \"" + GOOD_BOOK_ID + "\",\n" +
                "\t\"contentTitle\": \"" + DUMMY_BOOK_TITLE + "\",\n" +
                "\t\"artUrl\": \"" + DUMMY_BOOK_ART_URL + "\",\n" +
                "\t\"reviewTitle\": \"" + DUMMY_REVIEW_TITLE + "\",\n" +
                "\t\"reviewBody\": \"" + DUMMY_REVIEW_TITLE+ "\",\n" +
                "\t\"rating\": \"" + GOOD_DUMMY_RATING + "\"\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        new ReviewServlet().doPost(request, response);
        writer.flush();

        List<ReviewObject> reviews = ofy().load().type(ReviewObject.class)
                .filter("userId", DUMMY_USER_ID)
                .list();
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());

        reviews = ofy().load().type(ReviewObject.class)
                .filter("contentType", ContentType.BOOK)
                .filter("contentId", GOOD_BOOK_ID)
                .list();
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
    }

    @Test
    public void testPostDuplicateReview() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);
        String json = "{\n" +
                "\t\"authorId\": \"" + DUMMY_USER_ID + "\",\n" +
                "\t\"authorName\": \"" + DUMMY_USERNAME + "\",\n" +
                "\t\"contentType\": \"" + ContentType.BOOK + "\",\n" +
                "\t\"contentId\": \"" + GOOD_BOOK_ID + "\",\n" +
                "\t\"contentTitle\": \"" + DUMMY_BOOK_TITLE + "\",\n" +
                "\t\"artUrl\": \"" + DUMMY_BOOK_ART_URL + "\",\n" +
                "\t\"reviewTitle\": \"" + DUMMY_REVIEW_TITLE + "\",\n" +
                "\t\"reviewBody\": \"" + DUMMY_REVIEW_TITLE+ "\",\n" +
                "\t\"rating\": \"" + GOOD_DUMMY_RATING + "\"\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        new ReviewServlet().doPost(request, response);
        writer.flush();

        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));
        new ReviewServlet().doPost(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_CONFLICT);
    }

    @Test
    public void testDeleteUnauthenticated() throws IOException {
        initLoggedOut();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn(ContentType.MOVIE);
        when(request.getParameter("contentId")).thenReturn(GOOD_MOVIE_ID);

        new ReviewServlet().doDelete(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDeleteNullParameters() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);

        new ReviewServlet().doDelete(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDeleteEmptyParameters() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn("");
        when(request.getParameter("contentId")).thenReturn("");

        new ReviewServlet().doDelete(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDeleteBadMovie() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn(ContentType.MOVIE);
        when(request.getParameter("contentId")).thenReturn(BAD_MOVIE_ID);

        new ReviewServlet().doDelete(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testDeleteBadBook() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn(ContentType.BOOK);
        when(request.getParameter("contentId")).thenReturn(BAD_BOOK_ID);

        new ReviewServlet().doDelete(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testDeleteNoReview() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn(ContentType.BOOK);
        when(request.getParameter("contentId")).thenReturn(GOOD_BOOK_ID);

        new ReviewServlet().doDelete(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testDeleteGoodMovieReview() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn(ContentType.MOVIE);
        when(request.getParameter("contentId")).thenReturn(GOOD_MOVIE_ID);

        UserObject userObject = new UserObject(DUMMY_USER_ID, DUMMY_USERNAME, DUMMY_EMAIL, DUMMY_PROFILE_PIC_URL);
        ReviewObject reviewObject = new ReviewObject(userObject,
                ContentType.MOVIE, GOOD_MOVIE_ID,
                DUMMY_MOVIE_TITLE, DUMMY_MOVIE_ART_URL,
                DUMMY_REVIEW_TITLE, DUMMY_REVIEW_BODY, Integer.parseInt(GOOD_DUMMY_RATING));
        ofy().save().entity(reviewObject).now();
        List<ReviewObject> reviews = ofy().load().type(ReviewObject.class)
                .filter("userId", DUMMY_USER_ID)
                .list();
        assertFalse(reviews.isEmpty());

        new ReviewServlet().doDelete(request, response);
        writer.flush();

        reviews = ofy().load().type(ReviewObject.class)
                .filter("userId", DUMMY_USER_ID)
                .list();
        assertTrue(reviews.isEmpty());
    }

    @Test
    public void testDeleteGoodBookReview() throws IOException {
        initLoggedIn();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("contentType")).thenReturn(ContentType.BOOK);
        when(request.getParameter("contentId")).thenReturn(GOOD_BOOK_ID);

        UserObject userObject = new UserObject(DUMMY_USER_ID, DUMMY_USERNAME, DUMMY_EMAIL, DUMMY_PROFILE_PIC_URL);
        ReviewObject reviewObject = new ReviewObject(userObject,
                ContentType.BOOK, GOOD_BOOK_ID,
                DUMMY_BOOK_TITLE, DUMMY_BOOK_ART_URL,
                DUMMY_REVIEW_TITLE, DUMMY_REVIEW_BODY, Integer.parseInt(GOOD_DUMMY_RATING));
        ofy().save().entity(reviewObject).now();
        List<ReviewObject> reviews = ofy().load().type(ReviewObject.class)
                .filter("userId", DUMMY_USER_ID)
                .list();
        assertFalse(reviews.isEmpty());


        new ReviewServlet().doDelete(request, response);
        writer.flush();

        reviews = ofy().load().type(ReviewObject.class)
                .filter("userId", DUMMY_USER_ID)
                .list();
        assertTrue(reviews.isEmpty());
    }
}