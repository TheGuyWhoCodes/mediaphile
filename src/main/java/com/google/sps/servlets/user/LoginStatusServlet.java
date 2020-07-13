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
import com.google.sps.model.user.LoginStatus;

/** Servlet that handles user logins. */
@WebServlet("/login/status")
public class LoginStatusServlet extends HttpServlet {

    Gson gson = new Gson();

    /**
     * doGet() returns login details and url
     * @param request: no parameters
     * @param response: returns a LoginStatus object
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        UserService userService = UserServiceFactory.getUserService();

        boolean loggedIn = userService.isUserLoggedIn();
        String url = (loggedIn)
                ? "/login/register?logout=1"
                : userService.createLoginURL("/login/register");
        User user = userService.getCurrentUser();
        String id = (user != null) ? user.getUserId() : "";

        response.getWriter().println(gson.toJson(new LoginStatus(loggedIn, url, id)));
    }
}
