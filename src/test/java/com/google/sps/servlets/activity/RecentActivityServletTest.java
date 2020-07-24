package com.google.sps.servlets.activity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.ContextListener;
import com.google.sps.model.activity.Activity;
import com.google.sps.model.follow.FollowItem;
import com.google.sps.model.queue.QueueListItemObject;
import com.google.sps.model.queue.ViewedListItemObject;
import com.google.sps.model.review.ReviewObject;
import com.google.sps.model.user.UserObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RecentActivityServletTest {

    private ObjectMapper mapper = new ObjectMapper();
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private PrintWriter writer;
    private LocalServiceTestHelper helper;

    @Before
    public void before() throws IOException{
        new ContextListener().initDbObjects();
        Map<String, Object> attr = new HashMap<>();
        attr.put("com.google.appengine.api.users.UserService.user_id_key", "9876");

        helper =
                new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
                        new LocalUserServiceTestConfig())
                        .setEnvAttributes(attr)
                        .setEnvIsAdmin(true)
                        .setEnvIsLoggedIn(true)
                        .setEnvEmail("test@email.com")
                        .setEnvAuthDomain("mediaphile.com");
        helper.setUp();

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
    public void getEmptyList() {

    }

    @Test
    public void errorRequest() throws ServletException, IOException {
        addFollowers();
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("userId")).thenReturn("9876");

        new RecentActivityServlet().doGet(request, response);

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void getFilledList() throws ServletException, IOException {
        addFollowers();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("userId")).thenReturn("0123");
        when(request.getParameter("offset")).thenReturn("0");

        new RecentActivityServlet().doGet(request, response);

        writer.flush();

        List<QueueListItemObject> activityList = mapper.readValue(stringWriter.toString(), new TypeReference<List<QueueListItemObject>>(){});

        assertEquals( 1, activityList.size());
    }

    @Test
    public void emptyList() throws ServletException, IOException {
        addFollowers();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("userId")).thenReturn("9876");
        when(request.getParameter("offset")).thenReturn("0");

        new RecentActivityServlet().doGet(request, response);

        writer.flush();

        List<QueueListItemObject> activityList = mapper.readValue(stringWriter.toString(), new TypeReference<List<QueueListItemObject>>(){});

        assertEquals( 0, activityList.size());
    }
    private void addFollowers() {
        //bravo follows alpha
        FollowItem followers = new FollowItem();
        followers.setId(1234L);
        followers.setUserId("0123");
        followers.setTargetId("9876");

        ofy().save().entity(followers).now();

        //alpha follows charlie
        FollowItem following = new FollowItem();
        following.setId(4321L);
        following.setUserId("9876");
        following.setTargetId("3210");

        ofy().save().entity(following).now();

        UserObject alpha = new UserObject("9876", "alpha", "alpha@example.com", "");
        ofy().save().entity(alpha).now();

        UserObject bravo = new UserObject("0123", "bravo", "bravo@example.com", "");
        ofy().save().entity(bravo).now();

        UserObject charlie = new UserObject("3210", "charlie", "charlie@example.com", "");
        ofy().save().entity(charlie).now();

        QueueListItemObject queue = new QueueListItemObject();
        following.setId(4321L);
        queue.setArtUrl("http://fdsa");
        queue.setUserId("9876");
        ofy().save().entity(queue).now();
    }
}
