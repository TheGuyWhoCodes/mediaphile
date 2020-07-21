package com.google.sps.servlets.follow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.model.follow.FollowItem;
import com.google.sps.model.follow.FollowResponse;
import com.google.sps.util.Utils;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.google.sps.util.HttpUtils.*;
import static com.googlecode.objectify.ObjectifyService.ofy;

@WebServlet("/follow")
public class FollowServlet extends HttpServlet {
    private final UserService userService = UserServiceFactory.getUserService();
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        String userId = request.getParameter("userId");
        String listType = request.getParameter("listType");

        if(userId == null || userId.isEmpty() || listType == null 
            || !listType.equals(FollowItem.TYPE_FOLLOWERS)
            || !listType.equals(FollowItem.TYPE_FOLLOWING)) {
                setInvalidGetResponse(response);
            return;
        }

        List<FollowItem> result = getList(userId, listType);
        response.getWriter().println(gson.toJsonTree(result));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        FollowResponse newResponse = new FollowResponse();
        String body = Utils.collectRequestLines(request);

        User user = userService.getCurrentUser();
        if(!userService.isUserLoggedIn()) {
            sendNotLoggedIn(response);
            return;
        }

        String userId = user.getUserId();
        FollowItem newFollowItem = null;

        try {
            newFollowItem = mapper.readValue(body, FollowItem.class);
            if(!userId.equals(newFollowItem.getUserId())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ofy().save().entity(newFollowItem).now();
        /*try {
            ofy().save().entity(newFollowItem).now();
        } catch(Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }*/
        newResponse.setSuccess(true);
        newResponse.setEntity(newFollowItem);
        response.getWriter().println(gson.toJsonTree(newResponse));
    }

    /*@Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        User user = userService.getCurrentUser();
        if(!userService.isUserLoggedIn()) {
            sendNotLoggedIn(response);
            return;
        }

        String userId = user.getUserId();
        String followingId = request.getParameter("followingId");

        ofy().delete().type(FollowItem.class)
        .filter("followerId", userId)
        .filter("followingId", followingId).now();
        response.sendError(HttpServletResponse.SC_OK);
    }*/


    private List<FollowItem> getList(String userId, String followType) {
        if(followType.equals(FollowItem.TYPE_FOLLOWERS)) {
            //Sets user as target to retrieve followers.
            return ofy().load().type(FollowItem.class).filter("targetId", userId).list();
        } else {
            //Sets user as follower to retrieve following.
            return ofy().load().type(FollowItem.class).filter("userId", userId).list();
        }
    }
}