package com.google.sps.servlets.user;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.users.User;
import com.google.gson.Gson;

import static com.googlecode.objectify.ObjectifyService.ofy;

/** Servlet that returns user information.
 *  Returns additional information if the query is for the current user
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {

    Gson gson = new Gson();

    /**
     * doGet() returns details of the particular user with the given id
     * Returns error 400 if no id is provided
     * Returns error 404 if the user cannot be found
     * @param request: expects id parameter
     * @param response: returns a Volume object
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String id = request.getParameter("id");
        if (id == null || id.equals("")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UserObject userObject = ofy().load().type(UserObject.class).id(id).now();
        if (userObject == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (user == null || !user.getUserId().equals(id)) {
            userObject.setEmail("");
        }

        response.getWriter().println(gson.toJson(userObject));
    }
}
