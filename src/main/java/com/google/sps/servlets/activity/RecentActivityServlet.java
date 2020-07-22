package com.google.sps.servlets.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.sps.model.follow.FollowItem;
import com.google.sps.model.queue.QueueListItemObject;
import com.google.sps.model.queue.ViewedListItemObject;
import com.google.sps.model.review.ReviewObject;
import com.google.sps.model.user.UserObject;
import com.google.sps.util.HttpUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * @author chris
 * date: 7/21/2020
 */

@WebServlet("/activity/followers")
public class RecentActivityServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Object> results = new ArrayList<>();

        String userId = request.getParameter("userId");
        if (userId == null) {
            HttpUtils.setInvalidGetResponse(response);
            return;
        }
        List<String> following = getFollowingList(userId);

        List<ReviewObject> reviews = getFollowingReviews(following);
        List<ViewedListItemObject> viewed = getFollowingViewed(following);
        List<QueueListItemObject> queue = getFollowingQueue(following);
    }

    private List<String> getFollowingList(String userId) {
        List<FollowItem> userObjects = ofy().load().type(FollowItem.class).filter("userId", userId).list();
        return userObjects.stream().
                map(FollowItem::getUserId).collect(Collectors.toList());
    }

    private List<ReviewObject> getFollowingReviews(List<String> following) {
        return ofy().load().type(ReviewObject.class).filter("userId", following).list();
    }

    private List<QueueListItemObject> getFollowingQueue(List<String> following) {
        return ofy().load().type(QueueListItemObject.class).filter("userId", following).list();
    }

    private List<ViewedListItemObject> getFollowingViewed(List <String> following) {
        return ofy().load().type(ViewedListItemObject.class).filter("userId", following).list();
    }
}
