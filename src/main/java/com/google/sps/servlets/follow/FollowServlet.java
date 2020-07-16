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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@WebServlet("/follow/list")
public class FollowServlet extends HttpServlet {
    private final UserService userService = UserServiceFactory.getUserService();
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        String userId = request.getParameter("userId");
        String listType = request.getParameter("listType");

        /*if(!userId || !followType) {
            setInvalidGetResponse(response);
            return;
        }*/

        List<FollowItem> result = getList(userId, listType);
        response.getWriter().println(gson.toJsonTree(result));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        String targetId = request.getParameter("userId");
        String followingId = request.getParameter("followingId");

        try {
            response.getWriter().println(gson.toJsonTree(createFollowItem(targetId, followingId)));
        }
        catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    private List<FollowItem> getList(String userId, String followType) {
        if(followType.equals(FollowItem.TYPE_FOLLOWERS)) {
            //FollowingId as in the userId is being followed.
            return ofy().load().type(FollowItem.class).filter("followingId", userId).list();
        } else {
            //followerId as in the userId is following other users.
            return ofy().load().type(FollowItem.class).filter("followerId", userId).list();
        }
    }

    private FollowItem createFollowItem(String userId, String followingId) throws Exception {
        FollowItem followItem = new FollowItem(userId, followingId);
        ofy().save().entity(followItem).now();
        return followItem;
    }
}