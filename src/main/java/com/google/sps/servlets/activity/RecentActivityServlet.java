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

/**
 * @author chris
 * date: 7/21/2020
 */

@WebServlet("/activity/followers")
public class RecentActivityServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Gson gson = new Gson();
    private final int ACTIVITY_LIMIT = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int offset;

        String userId = request.getParameter("userId");
        if (userId == null) {
            HttpUtils.setInvalidGetResponse(response);
            return;
        }

        try {
            offset = Utils.parseInt(request.getParameter("offset"));
        } catch (NullPointerException e){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // grabs list of "following" related to a user, this is a list of user id (as string
        List<String> following = getFollowingList(userId);

        List<Activity> reviews = getActivity(following, offset);

        response.getWriter().println(gson.toJsonTree(reviews));
    }

    private List<String> getFollowingList(String userId) {
        List<FollowItem> userObjects = ofy().load().type(FollowItem.class).filter("userId", userId).list();
        return userObjects.stream().
                map(FollowItem::getUserId).collect(Collectors.toList());
    }

    private List<Activity> getActivity(List<String> following, int offset) {
        return ofy().load().type(Activity.class)
                .filter("userId", following)
                .order("-timestamp")
                .limit(ACTIVITY_LIMIT)
                .offset(offset)
                .list();
    }
}
