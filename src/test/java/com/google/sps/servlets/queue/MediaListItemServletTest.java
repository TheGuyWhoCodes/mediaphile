package com.google.sps.servlets.queue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.ContextListener;
import com.google.sps.model.queue.MediaListItem;
import com.google.sps.model.queue.QueueListItemObject;
import com.google.sps.model.queue.ViewedListItemObject;
import com.google.sps.servlets.TestDelegatingServletInputStream;
import com.google.sps.util.Utils.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static junit.framework.Assert.assertEquals;

public class MediaListItemServletTest extends Mockito {

    public static final String GOOD_MOVIE_ID = "127";

    private ObjectMapper mapper = new ObjectMapper();
    private LocalServiceTestHelper helper;

    @Before
    public void initialize() {
        new ContextListener().initDbObjects();
        Map<String, Object> attr = new HashMap<>();
        attr.put("com.google.appengine.api.users.UserService.user_id_key", "5678");

        helper =
                new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
                        new LocalUserServiceTestConfig())
                        .setEnvAttributes(attr)
                        .setEnvIsAdmin(true)
                        .setEnvIsLoggedIn(true)
                        .setEnvEmail("mediaphile@gmail.com")
                        .setEnvAuthDomain("mediaphile.com");
        helper.setUp();
        populateDb();
    }

    @After
    public void tearDown() {
        helper.tearDown();
        ofy().clear();
    }

    @Test
    public void getEntityWatched() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("userId")).thenReturn("5678");
        when(request.getParameter("listType")).thenReturn(MediaListItem.TYPE_VIEWED);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        new MediaListItemServlet().doGet(request, response);
        writer.flush();
        List<ViewedListItemObject> db = mapper.readValue(stringWriter.toString(), new TypeReference<List<ViewedListItemObject>>(){});
        assertEquals(1, db.size());
        assertEquals(MediaListItem.TYPE_VIEWED, db.get(0).getListType());
        assertEquals(ContentType.MOVIE, db.get(0).getMediaType());
        assertEquals("123", db.get(0).getMediaId());
    }

    @Test
    public void getEntityQueue() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("userId")).thenReturn("5678");
        when(request.getParameter("listType")).thenReturn(MediaListItem.TYPE_QUEUE);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        new MediaListItemServlet().doGet(request, response);
        writer.flush();
        List<ViewedListItemObject> db = mapper.readValue(stringWriter.toString(), new TypeReference<List<ViewedListItemObject>>(){});
        assertEquals(1, db.size());
        assertEquals(MediaListItem.TYPE_QUEUE, db.get(0).getListType());
        assertEquals(ContentType.BOOK, db.get(0).getMediaType());
        assertEquals("321", db.get(0).getMediaId());
    }

    @Test
    public void getEntityInvalidRequest() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("userId")).thenReturn("5678");
        when(request.getParameter("listType")).thenReturn("hmmm");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);
        new MediaListItemServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void postEntity() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String json = "{\n" +
                "\t\"mediaId\": " + GOOD_MOVIE_ID + ",\n" +
                "\t\"title\": \"afdsafdsafdsa cool\",\n" +
                "    \"mediaType\": \"movie\",\n" +
                "    \"listType\": \"queue\",\n" +
                "    \"artUrl\": \"hey.com/coolimage.png\",\n" +
                "    \"userId\": \"5678\"\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        assertEquals(1, ofy().load().type(QueueListItemObject.class).list().size());
        new MediaListItemServlet().doPost(request, response);
        writer.flush();
        assertEquals(2, ofy().load().type(QueueListItemObject.class).list().size());
    }

    @Test
    public void postDuplicateEntity() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String json = "{\n" +
                "\t\"mediaId\": " + GOOD_MOVIE_ID + ",\n" +
                "\t\"title\": \"afdsafdsafdsa cool\",\n" +
                "    \"mediaType\": \"movie\",\n" +
                "    \"listType\": \"queue\",\n" +
                "    \"artUrl\": \"hey.com/coolimage.png\",\n" +
                "    \"userId\": \"5678\"\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new MediaListItemServlet().doPost(request, response);
        writer.flush();

        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));
        new MediaListItemServlet().doPost(request, response);
        writer.flush();

        verify(response, times(1)).setStatus(HttpServletResponse.SC_CONFLICT);
    }

    @Test
    public void invalidPostEntity() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String json = "{\n" +
                "\t\"mediaId\": 15123,\n" +
                "\t\"title\": \"afdsafdsafdsa cool\",\n" +
                "    \"mediaType\": \"movie\",\n" +
                "    \"listType\": \"queue\",\n" +
                "    \"artUrl\": \"hey.com/coolimage.png\",\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);
        new MediaListItemServlet().doPost(request, response);
        writer.flush();
        assertEquals(ofy().load().type(QueueListItemObject.class).list().size(), 1);
        verify(response, times(1)).setStatus(400);
    }


    @Test
    public void deleteFromDbWatched() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("mediaType")).thenReturn(ContentType.MOVIE);
        when(request.getParameter("mediaId")).thenReturn("123");
        when(request.getParameter("listType")).thenReturn(MediaListItem.TYPE_VIEWED);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        assertEquals(1, ofy().load().type(QueueListItemObject.class).list().size());
        new MediaListItemServlet().doDelete(request, response);
        writer.flush();
        assertEquals(0, ofy().load().type(ViewedListItemObject.class).list().size());
    }

    @Test
    public void deleteFromDbQueued() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("mediaType")).thenReturn(ContentType.BOOK);
        when(request.getParameter("mediaId")).thenReturn("321");
        when(request.getParameter("listType")).thenReturn(MediaListItem.TYPE_QUEUE);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        assertEquals(1, ofy().load().type(QueueListItemObject.class).list().size());
        new MediaListItemServlet().doDelete(request, response);
        writer.flush();
        assertEquals(0, ofy().load().type(QueueListItemObject.class).list().size());
    }

    @Test
    public void invalidDeleteRequest() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        new MediaListItemServlet().doDelete(request, response);
        writer.flush();

        verify(response, times(1)).setStatus(400);
    }

    private void populateDb() {
        ViewedListItemObject watched = new ViewedListItemObject();
        watched.setId(1234L);
        watched.setUserId("5678");
        watched.setMediaType(ContentType.MOVIE);
        watched.setMediaId("123");
        watched.setListType(MediaListItem.TYPE_VIEWED);

        ofy().save().entity(watched).now();

        QueueListItemObject queue = new QueueListItemObject();
        queue.setId(4321L);
        queue.setUserId("5678");
        queue.setMediaType(ContentType.BOOK);
        queue.setMediaId("321");
        queue.setListType(MediaListItem.TYPE_QUEUE);

        ofy().save().entity(queue).now();
    }
}