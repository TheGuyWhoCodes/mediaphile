package com.google.sps.servlets.queue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.ContextListener;
import com.google.sps.model.queue.IsInListResponseObject;
import com.google.sps.model.queue.MediaListItem;
import com.google.sps.model.queue.QueueListItemObject;
import com.google.sps.model.queue.ViewedListItemObject;
import com.google.sps.servlets.review.ReviewServlet;
import com.google.sps.util.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MediaItemInServletTest {

    private ObjectMapper mapper = new ObjectMapper();
    private LocalServiceTestHelper helper;

    private HttpServletResponse response;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @Before
    public void initialize() throws IOException {
        response = mock(HttpServletResponse.class);

        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

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
    }

    @After
    public void tearDown() {
        helper.tearDown();
        ofy().clear();
    }

    @Test
    public void getQueueShouldBeTrue() throws IOException {
        populateDb();
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("mediaId")).thenReturn("123");
        when(request.getParameter("userId")).thenReturn("5678");

        new MediaItemInServlet().doGet(request, response);
        writer.flush();

        IsInListResponseObject db = mapper.readValue(stringWriter.toString(), IsInListResponseObject.class);
        assertTrue(db.isInViewed());
        assertFalse(db.isInQueue());
    }

    @Test
    public void getWatchedShouldBeTrue() throws IOException {
        populateDb();
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("mediaId")).thenReturn("321");
        when(request.getParameter("userId")).thenReturn("5678");

        new MediaItemInServlet().doGet(request, response);
        writer.flush();

        IsInListResponseObject db = mapper.readValue(stringWriter.toString(), IsInListResponseObject.class);
        assertFalse(db.isInViewed());
        assertTrue(db.isInQueue());
    }

    @Test
    public void getEmptyDatabase() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("mediaId")).thenReturn("123");
        when(request.getParameter("userId")).thenReturn("5678");

        new MediaItemInServlet().doGet(request, response);
        writer.flush();

        IsInListResponseObject db = mapper.readValue(stringWriter.toString(), IsInListResponseObject.class);
        assertFalse(db.isInViewed());
        assertFalse(db.isInQueue());
    }

    @Test
    public void badRequest() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("mediaId")).thenReturn("1234");

        new MediaItemInServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
    private void populateDb() {
        ViewedListItemObject watched = new ViewedListItemObject();
        watched.setId(1234L);
        watched.setUserId("5678");
        watched.setMediaType(Utils.ContentType.MOVIE);
        watched.setMediaId("123");
        watched.setListType(MediaListItem.TYPE_VIEWED);

        ofy().save().entity(watched).now();

        QueueListItemObject queue = new QueueListItemObject();
        queue.setId(4321L);
        queue.setUserId("5678");
        queue.setMediaType(Utils.ContentType.BOOK);
        queue.setMediaId("321");
        queue.setListType(MediaListItem.TYPE_QUEUE);

        ofy().save().entity(queue).now();
    }
}
