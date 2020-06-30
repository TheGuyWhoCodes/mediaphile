package com.google.sps.servlets.queue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.users.UserService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.ContextListener;
import com.google.sps.model.queue.WantToWatchQueueObject;
import com.google.sps.model.queue.WatchedQueueObject;
import com.google.sps.servlets.DelegatingServletInputStream;
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

public class EntityListServletTest extends Mockito {

    private ObjectMapper mapper = new ObjectMapper();
    private LocalServiceTestHelper helper;

    @Before
    public void initialize() {
        new ContextListener().initDbObjects();
        Map<String, Object> attr = new HashMap<>();
        attr.put("com.google.appengine.api.users.UserService.user_id_key", "0987");

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

    @Test
    public void getEntityWatched() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("userID")).thenReturn("5678");
        when(request.getParameter("entityType")).thenReturn("viewed");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        new EntityListServlet().doGet(request, response);
        writer.flush();
        List<WatchedQueueObject> db = mapper.readValue(stringWriter.toString(), new TypeReference<List<WatchedQueueObject>>(){});
        assertEquals(1, db.size());
        assertEquals(1234, db.get(0).getEntityId());
        assertEquals("movie", db.get(0).getType());
    }

    @Test
    public void getEntityQueue() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("userID")).thenReturn("5678");
        when(request.getParameter("entityType")).thenReturn("queue");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        new EntityListServlet().doGet(request, response);
        writer.flush();
        List<WatchedQueueObject> db = mapper.readValue(stringWriter.toString(), new TypeReference<List<WatchedQueueObject>>(){});
        assertEquals(1, db.size());
        assertEquals(1234, db.get(0).getEntityId());
        assertEquals("book", db.get(0).getType());
        assertEquals("queue", db.get(0).getEntityType());
    }

    @Test
    public void getEntityInvalidRequest() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("userID")).thenReturn("5678");
        when(request.getParameter("entityType")).thenReturn("hmmm");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);
        new EntityListServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void postEntity() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String json = "{\n" +
                "\t\"id\": 15123,\n" +
                "\t\"title\": \"afdsafdsafdsa cool\",\n" +
                "    \"type\": \"movie\",\n" +
                "    \"entityType\": \"queue\",\n" +
                "    \"artUrl\": \"hey.com/coolimage.png\",\n" +
                "    \"userID\": \"0987\"\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new DelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);
        new EntityListServlet().doPost(request, response);
        writer.flush();
        assertEquals(2, ofy().load().type(WantToWatchQueueObject.class).list().size());
    }

    @Test
    public void invalidPostEntity() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String json = "{\n" +
                "\t\"id\": 15123,\n" +
                "\t\"title\": \"afdsafdsafdsa cool\",\n" +
                "    \"type\": \"movie\",\n" +
                "    \"entityType\": \"queue\",\n" +
                "    \"artUrl\": \"hey.com/coolimage.png\",\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new DelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);
        new EntityListServlet().doPost(request, response);
        writer.flush();
        assertEquals(ofy().load().type(WantToWatchQueueObject.class).list().size(), 1);
        verify(response, times(1)).setStatus(400);
    }

    private void populateDb() {
        WatchedQueueObject watched = new WatchedQueueObject();
        watched.setEntityId(1234);
        watched.setUserID("5678");
        watched.setType("movie");
        watched.setEntityType("viewed");

        ofy().save().entity(watched).now();

        WantToWatchQueueObject queue = new WantToWatchQueueObject();
        queue.setEntityId(1234);
        queue.setUserID("5678");
        queue.setType("book");
        queue.setEntityType("queue");

        ofy().save().entity(queue).now();
    }
}