package com.google.sps.servlets.follow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.cmd.QueryKeys;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.model.follow.FollowItem;
import com.google.sps.model.follow.FollowResponse;
import com.google.sps.model.follow.FollowListObject;
import com.google.sps.model.user.UserObject;
import com.google.sps.util.Utils;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator; 
import java.util.List;
import java.util.function.Consumer;
import java.lang.Iterable;

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
        String section = request.getParameter("pageNum");
        int page;

        if(userId == null || userId.isEmpty()) {
                setInvalidGetResponse(response);
            return;
        }

        //page start at 0
        if(section == null || section.isEmpty()) {
            page = 0;
        } else {
            page = 20 * Integer.parseInt(section);
        }

        List<FollowItem> followers = getList(userId, FollowItem.TYPE_FOLLOWERS, page);
        List<FollowItem> following = getList(userId, FollowItem.TYPE_FOLLOWING, page);

        List<UserObject> followerUserObjects = convertToUserObject(gson.toJsonTree(followers), "userId");
        List<UserObject> targetUserObjects = convertToUserObject(gson.toJsonTree(following), "targetId");

        int numFollowers = getListCount(userId, "targetId");
        int numFollowing = getListCount(userId, "userId");

        FollowListObject result = new FollowListObject(followerUserObjects, targetUserObjects,
         numFollowers, numFollowing);

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

        if(Iterables.size(getUserItems(newFollowItem.getUserId(), newFollowItem.getTargetId())) != 0) {
            response.sendError(HttpServletResponse.SC_CONFLICT);
            return;
        }

        try {
            ofy().save().entity(newFollowItem).now();
        } catch(Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        newResponse.setSuccess(true);
        newResponse.setEntity(newFollowItem);
        response.getWriter().println(gson.toJsonTree(newResponse));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        User user = userService.getCurrentUser();
        if(!userService.isUserLoggedIn()) {
            sendNotLoggedIn(response);
            return;
        }

        String userId = user.getUserId();
        String followingId = request.getParameter("followingId");

        if(followingId == null || followingId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        QueryKeys<?> targetKey = getUserItems(userId, followingId);

        if(Iterables.size(targetKey) == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ofy().delete().keys(targetKey).now();
        response.sendError(HttpServletResponse.SC_OK);
    }


    private List<FollowItem> getList(String userId, String followType, int page) {
        if(followType.equals(FollowItem.TYPE_FOLLOWERS)) {
            //Sets user as target to retrieve followers.
            return ofy().load().type(FollowItem.class).limit(20).offset(page).filter("targetId", userId).list();
        } else {
            //Sets user as follower to retrieve following.
            return ofy().load().type(FollowItem.class).limit(20).offset(page).filter("userId", userId).list();
        }
    }

    private List<UserObject> convertToUserObject(JsonElement followList, String type) {
        JsonArray arrayList = followList.getAsJsonArray();
        List<UserObject> userObject = new ArrayList<UserObject>();

        for(int i=0; i < arrayList.size(); i++) {
            String id = (((JsonObject)arrayList.get(0)).get(type)).toString();
            UserObject user = ofy().load().type(UserObject.class).id(id.substring(1, id.length()-1)).now();
            user.setEmail("");
            userObject.add(user);
        }
        return userObject;
    }

    public int getListCount(String userId, String type) {
        int size = 
        ofy().load().type(FollowItem.class).filter(type, userId).count();
        return size;
    }

    private QueryKeys<?> getUserItems(String userId, String targetId) {
        return ofy().load().type(FollowItem.class)
            .filter("userId", userId)
            .filter("targetId", targetId).keys();
    }
}