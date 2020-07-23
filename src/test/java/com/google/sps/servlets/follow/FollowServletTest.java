package com.google.sps.servlets.follow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.ContextListener;
import com.google.sps.model.user.UserObject;
import com.google.sps.model.follow.FollowItem;
import com.google.sps.model.follow.FollowListObject;
import com.google.sps.servlets.TestDelegatingServletInputStream;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
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

public class FollowServletTest extends Mockito {
    private LocalServiceTestHelper helper;
    private ObjectMapper mapper = new ObjectMapper();
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private PrintWriter writer;

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

        addFollowers();
    }

    @After
    public void tearDown() {
        helper.tearDown();
        ofy().clear();
    }

    @Test
    public void testGetFollowersList() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("userId")).thenReturn("9876");

        new FollowServlet().doGet(request, response);
        writer.flush();
        String expected = "{\"id\":null," +
        "\"followersList\":[{\"id\":\"0123\",\"username\":\"bravo\",\"email\":\"\",\"profilePicUrl\":\"\"}]," +
        "\"followingList\":[{\"id\":\"3210\",\"username\":\"charlie\",\"email\":\"\",\"profilePicUrl\":\"\"}]," +
        "\"followerLength\":1,\"followingLength\":1}";
        
        assertEquals(stringWriter.toString().trim(), expected);
    }

    @Test
    public void testGetEmptyId() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getParameter("userId")).thenReturn("");

        new FollowServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("userId");
        writer.flush();

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void testGetNullId() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        new FollowServlet().doGet(request, response);
        verify(request, atLeast(1)).getParameter("userId");
        writer.flush();

        verify(response, times(1)).setStatus(400);
    }

    @Test
    public void testPostFollowing() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        //alpa follows bravo back.
        String json = "{\n" +
                "\t\"userId\": \"9876\",\n" +
                "\t\"targetId\": \"0123\"\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        assertEquals(1, ofy().load().type(FollowItem.class).filter("userId", "9876").list().size());
        new FollowServlet().doPost(request,response);
        writer.flush();
        assertEquals(2, ofy().load().type(FollowItem.class).filter("userId", "9876").list().size());
    }

    @Test
    public void testPostDuplicateFollowing() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        //alpha follows charlie again.
        String json = "{\n" +
                "\t\"userId\": \"9876\",\n" +
                "\t\"targetId\": \"3210\"\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        new FollowServlet().doPost(request,response);
        writer.flush();
        
        verify(response, times(1)).sendError(409);
    }

    @Test
    public void testDeleteFollowing() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        //alpha unfollows charlie
        when(request.getParameter("followingId")).thenReturn("3210");

        assertEquals(2, ofy().load().type(FollowItem.class).list().size());
        new FollowServlet().doDelete(request, response);
        writer.flush();
        assertEquals(1, ofy().load().type(FollowItem.class).list().size());
    }

    @Test
    public void testNoDeleteTarget() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        new FollowServlet().doDelete(request, response);
        writer.flush();

        verify(response, times(1)).sendError(400);
    }

    @Test
    public void testPostUnauthenticated() throws IOException{
        helper.setEnvIsLoggedIn(false).setUp();
        HttpServletRequest request = mock(HttpServletRequest.class);

        String json = "{\n" +
                "\t\"userId\": \"3210\",\n" +
                "\t\"targetId\": \"0123\"\n" +
                "}";
        when(request.getInputStream()).thenReturn(
                new TestDelegatingServletInputStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))));
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(json)));

        new FollowServlet().doPost(request,response);
        writer.flush();
        
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
    }
}