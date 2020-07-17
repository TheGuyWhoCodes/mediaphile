package com.google.sps.servlets.user;

import com.google.sps.ContextListener;
import com.google.sps.model.user.UserObject;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.*;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class LoginRegistrationServletTest extends Mockito {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @BeforeClass
    public static void initialize() {
        new ContextListener().initDbObjects();
    }

    @After
    public void tearDown() {
        helper.tearDown();
        ofy().clear();
    }

    /**
     * Ensure error 400 is thrown when the user is not logged in
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

        new LoginRegistrationServlet().doGet(request, response);
        writer.flush();

        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Ensure a new logged in user gets registered
     * @throws IOException
     */
    @Test
    public void testLoggedInUser() throws IOException {
        Map<String, Object> attr = new HashMap<>();
        String id = "123";
        attr.put("com.google.appengine.api.users.UserService.user_id_key", id);
        helper.setEnvEmail("test@example.com")
                .setEnvAttributes(attr)
                .setEnvAuthDomain("example.com")
                .setEnvIsLoggedIn(true)
                .setUp();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        writer.flush();
        when(response.getWriter()).thenReturn(writer);

        assertNull(ofy().load().type(UserObject.class).id(id).now());

        new LoginRegistrationServlet().doGet(request, response);
        writer.flush();

        verify(response, atLeast(1)).sendRedirect(captor.capture());
        assertEquals("/home", captor.getValue());

        UserObject result = ofy().load().type(UserObject.class).id(id).now();

        assertEquals(result.getId(), id);
        assertEquals(result.getUsername(), "test");
        assertEquals(result.getEmail(), "test@example.com");
        assertEquals(result.getProfilePicUrl(), "");
    }

    /**
     * Ensure an already registered user is not modified
     * @throws IOException
     */
    @Test
    public void testRegisteredUser() throws IOException {
        Map<String, Object> attr = new HashMap<>();
        String id = "123";
        attr.put("com.google.appengine.api.users.UserService.user_id_key", id);
        helper.setEnvEmail("test@example.com")
                .setEnvAttributes(attr)
                .setEnvAuthDomain("example.com")
                .setEnvIsLoggedIn(true)
                .setUp();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        writer.flush();
        when(response.getWriter()).thenReturn(writer);

        assertNull(ofy().load().type(UserObject.class).id(id).now());

        new LoginRegistrationServlet().doGet(request, response);
        writer.flush();

        verify(response, atLeast(1)).sendRedirect(captor.capture());
        assertEquals("/home", captor.getValue());

        // Duplicate request shouldn't change the result
        new LoginRegistrationServlet().doGet(request, response);
        writer.flush();

        verify(response, atLeast(1)).sendRedirect(captor.capture());
        assertEquals("/home", captor.getValue());

        UserObject result = ofy().load().type(UserObject.class).id(id).now();

        assertEquals(result.getId(), id);
        assertEquals(result.getUsername(), "test");
        assertEquals(result.getEmail(), "test@example.com");
        assertEquals(result.getProfilePicUrl(), "");
    }
}
