package com.google.sps.servlets.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.sps.ContextListener;
import info.movito.themoviedbapi.model.MovieDb;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class LoginServletTest extends Mockito {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final Gson gson = new Gson();
    private final ObjectMapper mapper = new ObjectMapper();

    @After
    public void tearDown() {
        helper.tearDown();
        ofy().clear();
    }

    /**
     * Ensure only login URL is returned to logged out user
     * @throws IOException
     */
    @Test
    public void testLoggedOutUser() throws IOException {
        helper.setEnvIsLoggedIn(false)
                .setUp();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        writer.flush();
        when(response.getWriter()).thenReturn(writer);

        new LoginServlet().doGet(request, response);
        writer.flush();
        LoginStatus result = mapper.readValue(stringWriter.toString(), LoginStatus.class);

        assertFalse(result.isLoggedIn());
        assertFalse(result.getUrl().isEmpty());
        assertTrue(result.getId().isEmpty());
    }

    /**
     * Ensure all information is returned when the user is logged in
     * @throws IOException
     */
    @Test
    public void testLoggedInUser() throws IOException {
        Map<String, Object> attr = new HashMap<>();
        attr.put("com.google.appengine.api.users.UserService.user_id_key", "123");
        helper.setEnvEmail("test@example.com")
                .setEnvAttributes(attr)
                .setEnvAuthDomain("example.com")
                .setEnvIsLoggedIn(true)
                .setUp();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        writer.flush();
        when(response.getWriter()).thenReturn(writer);

        new LoginServlet().doGet(request, response);
        writer.flush();
        LoginStatus result = mapper.readValue(stringWriter.toString(), LoginStatus.class);

        assertTrue(result.isLoggedIn());
        assertFalse(result.getUrl().isEmpty());
        assertEquals(result.getId(), "123");
    }
}
