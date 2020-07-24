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
    private static final int RESULSTS_PER_PAGE = 20;
    private final UserService userService = UserServiceFactory.getUserService();
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * doGet() returns two lists of userobjects, one will be followers while 
     * the other will be a following list by a given user or of a given media item
     * if followingId is provided it returns if the user is following that user or not
     * Expects either ?userId={id} or ?userId={id}&pageNumber{pageNumber}
     * Returns error 400 if the query parameters are not in either of these formats
     * Returns error 400 if a parameter is empty
     * Simply returns an empty list if the given media ID does not exist to avoid API call
     * @param request: expects userId, pageNumber and followingId is optional
     * @param response: returns a JSON object of FollowListObject
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        String userId = request.getParameter("userId");
        String pageNumber = request.getParameter("pageNumber");
        String targetId = request.getParameter("targetId");
        int startIndex;

        if(userId == null || userId.isEmpty()) {
            setInvalidGetResponse(response);
            return;
        }

        if(targetId != null) {
            QueryKeys<?> targetKey = getUserItems(userId, targetId);
            
            if(Iterables.size(targetKey) != 0) {
                response.getWriter().append("true");
                return;
            } 
            response.getWriter().append("false");
            return;
        }

        //page start at 0
        if(pageNumber == null || pageNumber.isEmpty()) {
            startIndex = 0;
        } else {
            startIndex = RESULSTS_PER_PAGE * Integer.parseInt(pageNumber);
        }

        List<FollowItem> followers = getList(userId, FollowItem.TYPE_FOLLOWERS, startIndex);
        List<FollowItem> following = getList(userId, FollowItem.TYPE_FOLLOWING, startIndex);

        List<UserObject> followerUserObjects = convertToUserObject(gson.toJsonTree(followers), "userId");
        List<UserObject> targetUserObjects = convertToUserObject(gson.toJsonTree(following), "targetId");

        int numFollowers = getListCount(userId, "targetId");
        int numFollowing = getListCount(userId, "userId");

        FollowListObject result = new FollowListObject(followerUserObjects, targetUserObjects,
            numFollowers, numFollowing);

        response.getWriter().println(gson.toJsonTree(result));
    }

    /**
     * doPost() attempts to add a given user to target follow item to the FollowItem list
     * Returns error 400 if the body or any of its fields is invalid
     * Returns error 401 if user is not authenticated
     * Returns error 404 if the target cannot be found
     * Returns error 409 if object has already been posted
     * Returns error 500 if there is an error sending the response
     * @param request: expects a body containing the fields of FollowItem, excluding its unique ID
     * @param response: returns a copy of the POSTed item on success
     * @throws IOException
     */
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

        UserObject target = ofy().load().type(UserObject.class).id(newFollowItem.getTargetId()).now();
        if (target == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
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

    /**
     * doDelete() attempts to delete the follow relationship between the
     * current logged in user and the user they are following
     * Returns error 400 if any parameters are invalid
     * Returns error 401 if user is not authenticated
     * Returns error 500 if an error occurs with deletion
     * @param request: expects followingId
     * @param response: returns OK code on success
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        User user = userService.getCurrentUser();
        if(!userService.isUserLoggedIn()) {
            sendNotLoggedIn(response);
            return;
        }

        String userId = user.getUserId();
        String targetId = request.getParameter("followingId");

        if(targetId == null || targetId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        QueryKeys<?> targetKey = getUserItems(userId, targetId);

        if(Iterables.size(targetKey) == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ofy().delete().keys(targetKey).now();
        response.sendError(HttpServletResponse.SC_OK);
    }


    /**
     * A helper function to get entity data from database
     * @param followType: what list type should it look for (followers / following)
     * @param userId: userId to look for
     * @param startIndex: from wich index to return the list from
     * @return: a list of abstracted type EntityDbQueue
     */
    private List<FollowItem> getList(String userId, String followType, int startIndex) {
        if(followType.equals(FollowItem.TYPE_FOLLOWERS)) {
            //Sets user as target to retrieve followers.
            return ofy().load().type(FollowItem.class)
                .limit(RESULSTS_PER_PAGE)
                .offset(startIndex)
                .filter("targetId", userId).list();
        } else {
            //Sets user as follower to retrieve following.
            return ofy().load().type(FollowItem.class)
                .limit(RESULSTS_PER_PAGE)
                .offset(startIndex)
                .filter("userId", userId).list();
        }
    }

    /**
     * Converts FollowItems id list to UserObject list
     * @param followList: the list to be converted to UserObject
     * @param type: userId or targetId to look for
     * @return: a list of UserObject
     */
    private List<UserObject> convertToUserObject(JsonElement followList, String type) {
        JsonArray arrayList = followList.getAsJsonArray();
        List<UserObject> userObject = new ArrayList<UserObject>();

        for(int i=0; i < arrayList.size(); i++) {
            String userId = (((JsonObject)arrayList.get(i)).get(type)).toString();
            //removes "quotes" from user id (ex. "123" => 123)
            String id = userId.substring(1, userId.length()-1);
            UserObject user = ofy().load().type(UserObject.class).id(id).now();
            user.setEmail("");
            userObject.add(user);
        }
        return userObject;
    }

    public int getListCount(String userId, String type) {
        return 
            ofy().load().type(FollowItem.class).filter(type, userId).count();
    }

    /**
     * A helper function to return a list of keys from FollowItem list
     * @param userId: userId to look for
     * @param targetId: targetId to look for
     * @return: a set of QueryKeys
     */
    private QueryKeys<?> getUserItems(String userId, String targetId) {
        return ofy().load().type(FollowItem.class)
            .filter("userId", userId)
            .filter("targetId", targetId).keys();
    }
}