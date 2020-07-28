package com.google.sps.servlets.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.sps.model.activity.Activity;
import com.google.sps.model.follow.FollowItem;
import com.google.sps.util.HttpUtils;
import com.google.sps.util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.googlecode.objectify.ObjectifyService.ofy;

@WebServlet("/activity/followers")
public class RecentActivityServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final int ACTIVITY_LIMIT = 10;

    /**
     * doGet() is an endpoint that takes in two params, a userId to check, and an offset (how many pages the user is in)
     * The doGet() will return a 400 error if the user is missing any query params, if not, then it'll return an empty
     * arraylist if there is no activity, or an arraylist of activity objects if there is activity
     * @param request: request coming in from user with userId and offset
     * @param response: an arraylist of Activity objects
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int pageNumber;

        String userId = request.getParameter("userId");
        if (userId == null) {
            HttpUtils.setInvalidGetResponse(response);
            return;
        }

        try {
            pageNumber = Utils.parseInt(request.getParameter("pageNumber"));
        } catch (NullPointerException e){
            HttpUtils.setInvalidGetResponse(response);
            return;
        }

        // grabs list of "following" related to a user, this is a list of user id (as string
        List<String> following = getFollowingList(userId);

        List<Activity> reviews = getActivity(following, pageNumber);

        response.getWriter().println(gson.toJsonTree(reviews));
    }

    /**
     * Helper function to get a users following list, used to feed into objectify
     * @param userId: the user id to get followers from
     * @return: an arraylist of following of a userID
     */
    private List<String> getFollowingList(String userId) {
        List<FollowItem> userObjects = ofy().load().type(FollowItem.class).filter("userId", userId).list();
        return userObjects.stream().
                map(FollowItem::getTargetId).collect(Collectors.toList());
    }

    /**
     * A function to get a following activity (could be list activity or reviews)
     * @param following: an arraylist of following userIds
     * @param offset: number to offset by, goes in 10s
     * @return: an arraylist of activity given the list of following
     */
    private List<Activity> getActivity(List<String> following, int offset) {
        if(following.size() == 0) {
            return new ArrayList<Activity>();
        }
        return ofy().load().type(Activity.class)
                .filter("userId IN", following)
                .order("-timestamp")
                .limit(ACTIVITY_LIMIT)
                .offset(offset)
                .list();
    }
}
