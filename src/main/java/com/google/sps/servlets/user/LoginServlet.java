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

/** Servlet that handles user logins. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private class LoginStatus {
        private boolean logged_in;
        private String url;
        private String id;

        public LoginStatus(boolean logged_in, String url, String id) {
            this.logged_in = logged_in;
            this.url = url;
            this.id = id;
        }
    }

    String makeJSONStatus(boolean logged_in, String url, String id) {
        Gson gson = new Gson();
        String json = gson.toJson(new LoginStatus(logged_in, url, id));
        return json;
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

        boolean logged_in = userService.isUserLoggedIn();
        String url = (logged_in)
                ? userService.createLogoutURL("/")
                : userService.createLoginURL("/login/callback");
        User user = userService.getCurrentUser();
        String id = (user != null) ? user.getUserId() : "";

        response.getWriter().println(makeJSONStatus(logged_in, url, id));
    }
}
