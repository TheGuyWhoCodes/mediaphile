package com.google.sps.servlets.review;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.google.sps.model.review.ReviewObject;
import com.google.sps.model.user.UserObject;

import static com.google.sps.util.Utils.mediaItemExists;
import static com.google.sps.util.Utils.parseInt;
import static com.googlecode.objectify.ObjectifyService.ofy;

@WebServlet("/reviews")
public class ReviewServlet extends HttpServlet {

    private final Gson gson = new Gson();

    /**
     * doGet() returns details of the reviews by a given user or of a given media item
     * Expects either ?contentType={book | movie}&contentId={id}  OR ?userId={id}
     * Returns error 400 if the query parameters are not in either of these formats
     * Returns error 400 if a parameter is empty or invalid (e.g. "bok")
     * Returns error 404 if the given user is not found
     * Simply returns an empty list if the given media ID does not exist to avoid API call
     * @param request: expects contentType&contentId OR userId
     * @param response: returns a JSON list of ReviewObject
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        String userId = request.getParameter("userId");
        String contentType = request.getParameter("contentType");
        String contentId = request.getParameter("contentId");

        List<ReviewObject> reviews;
        if (userId != null && contentType == null && contentId == null) {
            if (userId.equals("")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if (ofy().load().type(UserObject.class).id(userId).now() == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // TODO: Unify capitalization of userId with EntityListServlet
            // UserService uses `userId` as per Google Style Guide definition of CamelCase
            reviews = ofy().load().type(ReviewObject.class)
                    .filter("authorId", userId)
                    .list();
        }
        else if (userId == null && contentType != null && contentId != null) {
            if (contentId.equals("")
                    || !(contentType.equals("book") || contentType.equals("movie"))) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            reviews = ofy().load().type(ReviewObject.class)
                    .filter("contentType", contentType)
                    .filter("contentId", contentId)
                    .list();
        }
        else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.getWriter().println(gson.toJson(reviews));
    }

    /**
     * doPost() attempts to post a review for a given item from a user
     * Returns error 400 if any parameters are invalid
     * Returns error 401 if user is not authenticated
     * @param request: expects contentType, contentId, reviewTitle, reviewBody, and rating
     * @param response: returns a JSON string of the review if successful
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UserObject userObject = ofy().load().type(UserObject.class).id(user.getUserId()).now();
        if (userObject == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Integer rating = parseInt(request.getParameter("rating"));
        if (rating == null || !(1 <= rating && rating <= 5)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String contentType = request.getParameter("contentType");
        String contentId = request.getParameter("contentId");
        if (contentType == null || contentId == null
                || contentId.equals("")
                || !(contentType.equals("book") || contentType.equals("movie"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String reviewTitle = request.getParameter("reviewTitle");
        String reviewBody = request.getParameter("reviewBody");
        if (reviewTitle == null || reviewBody == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Boolean itemExists = mediaItemExists(contentType, contentId);
        if (itemExists == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        else if (!itemExists) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            ReviewObject reviewObject = new ReviewObject(userObject,
                    contentType, contentId,
                    reviewTitle, reviewBody, rating);
            ofy().save().entity(reviewObject).now();
            response.getWriter().println(gson.toJsonTree(reviewObject));
        }
        catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
    }
}