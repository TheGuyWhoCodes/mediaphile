package com.google.sps.servlets.user;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.users.User;
import com.google.gson.Gson;

/** Servlet that returns user information.
 *  Returns additional information if the query is for the current user
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {

    Gson gson = new Gson();

    public static class UserEntity {
        String id;
        String username;
        String email;
        String profilePicUrl;

        public UserEntity(String id, String username, String email, String profilePicUrl) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.profilePicUrl = profilePicUrl;
        }
    }

    // Search Datastore for an entry for the user
    public static Entity checkDatastore(String id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("User")
                .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
        PreparedQuery results = datastore.prepare(query);
        try {
            return results.asSingleEntity();
        }
        catch (Exception e) {
            return null;
        }
    }

    private UserEntity getStoredUser(String id) {
        Entity entity = checkDatastore(id);
        if (entity == null) return null;

        return new UserEntity(
            (String) entity.getProperty("id"),
            (String) entity.getProperty("username"),
            (String) entity.getProperty("email"),
            (String) entity.getProperty("profilePicUrl")
        );
    }

    /**
     * doGet() returns details of the particular user with the given id
     * Returns error 400 if no id is provided
     * Returns error 500 if the user cannot be found
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

        UserEntity userEntity = getStoredUser(id);
        if (userEntity == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (user == null || !user.getUserId().equals(id)) {
            userEntity.email = "";
        }

        response.getWriter().println(gson.toJson(userEntity));
    }
}
