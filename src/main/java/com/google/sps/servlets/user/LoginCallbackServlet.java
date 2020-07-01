package com.google.sps.servlets.user;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.users.User;

import static com.googlecode.objectify.ObjectifyService.ofy;

/** Servlet that handles user logins. */
@WebServlet("/login/callback")
public class LoginCallbackServlet extends HttpServlet {

    void storeUserIfNotFound(String id, String username, String email, String profilePicUrl) {
        if (ofy().load().type(UserObject.class).id(id).now() != null) return;
        ofy().save().entity(new UserObject(id, username, email, profilePicUrl)).now();
    }

    /**
     * doGet() returns details of the particular volume with the given id
     * Returns error 400 if no id is provided
     * Returns error 500 if no movie is returned by the API
     * @param request: expects id parameter
     * @param response: returns a Volume object
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        storeUserIfNotFound(user.getUserId(), user.getEmail().split("@")[0], user.getEmail(), "");

        response.sendRedirect("/");
    }
}
