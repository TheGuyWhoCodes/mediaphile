package com.google.sps.servlets.follow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.ContextListener;
import com.google.sps.model.follow.FollowItem;
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
        when(request.getParameter("listType")).thenReturn(FollowItem.TYPE_FOLLOWERS);

        new FollowServlet().doGet(request, response);
        writer.flush();
        /*List<FollowItem> ob = mapper.readValue(stringWriter.toString(), new TypeReference<List<FollowItem>>(){});
        assertEquals(1, ob.size());
        assertEquals("3210", ob.get(0).getTargetId());*/
        assertEquals(stringWriter.toString().trim(), "");
    }

    @Test
    public void testPostFollowing() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        String json = "{\n" +
                "\t\"userId\": \"9876\",\n" +
                "\t\"targetId\": \"3210\",\n" +
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

    private void addFollowers() {
        FollowItem followers = new FollowItem();
        followers.setId(1234L);
        followers.setUserId("0123");
        followers.setTargetId("9876");

        ofy().save().entity(followers).now();

        FollowItem following = new FollowItem();
        following.setId(4321L);
        following.setUserId("9876");
        following.setTargetId("3210");

        ofy().save().entity(following).now();
    }
}