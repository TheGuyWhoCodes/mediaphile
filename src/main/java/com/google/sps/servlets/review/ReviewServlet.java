package com.google.sps.servlets.review;

import com.google.api.services.books.model.Volume;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.google.sps.model.queue.WantToWatchQueueObject;
import com.google.sps.model.review.ReviewObject;
import com.google.sps.model.user.UserObject;
import com.google.sps.servlets.book.BookDetailsServlet;
import com.google.sps.servlets.movie.MovieDetailsServlet;
import com.google.sps.util.Utils.ContentType;
import com.googlecode.objectify.cmd.QueryKeys;
import info.movito.themoviedbapi.model.MovieDb;

import static com.google.sps.util.Utils.ContentType.isType;
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
            sendUserReviews(userId, response);
        }
        else if (userId == null && contentType != null && contentId != null) {
            sendContentReviews(contentType, contentId, response);
        }
        else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * doPost() attempts to post a review for a given item from a user
     * Returns error 400 if any parameters are invalid
     * Returns error 401 if user is not authenticated
     * Returns error 409 if the user already has a review for the item
     * Returns error 500 if an error occurs with response writing
     * @param request: expects contentType, contentId, reviewTitle, reviewBody, and rating
     * @param response: returns a JSON string of the review if successful
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        UserObject userObject = getUserObject();
        if (userObject == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String contentType = request.getParameter("contentType");
        String contentId = request.getParameter("contentId");
        String reviewTitle = request.getParameter("reviewTitle");
        String reviewBody = request.getParameter("reviewBody");
        Integer rating = parseInt(request.getParameter("rating"));
        Boolean itemExists = mediaItemExists(contentType, contentId);

        if (!validateParameters(contentType, contentId, reviewTitle, reviewBody, rating)
                || itemExists == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        else if (!itemExists) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (Iterables.size(getMatchingReviews(userObject.getId(), contentType, contentId)) != 0) {
            response.sendError(HttpServletResponse.SC_CONFLICT);
            return;
        }

        try {
            response.getWriter().println(gson.toJsonTree(
                    createAndSaveReview(userObject, contentType, contentId, reviewTitle, reviewBody, rating)));
        }
        catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private QueryKeys<ReviewObject> getMatchingReviews(String authorId, String contentType, String contentId) {
        return ofy().load().type(ReviewObject.class)
                .filter("authorId", authorId)
                .filter("contentType", contentType)
                .filter("contentId", contentId).keys();
    }

    private void sendUserReviews(String userId, HttpServletResponse response) throws IOException {
        if (userId.equals("")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        else if (ofy().load().type(UserObject.class).id(userId).now() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        else {
            List<ReviewObject> reviews = ofy().load().type(ReviewObject.class)
                    .filter("authorId", userId)
                    .list();
            response.getWriter().println(gson.toJson(reviews));
        }
    }

    private void sendContentReviews(String contentType, String contentId,
                                    HttpServletResponse response) throws IOException {
        if (contentId.equals("") || !isType(contentType)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        else {
            List<ReviewObject> reviews = ofy().load().type(ReviewObject.class)
                    .filter("contentType", contentType)
                    .filter("contentId", contentId)
                    .list();
            response.getWriter().println(gson.toJson(reviews));
        }
    }

    private UserObject getUserObject() {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        return (user == null) ? null : ofy().load().type(UserObject.class).id(user.getUserId()).now();
    }

    private boolean validateParameters(String contentType, String contentId,
                                       String reviewTitle, String reviewBody, Integer rating) {
        if (contentType == null || contentId == null) return false;
        if (contentId.isEmpty()) return false;
        if (!isType(contentType)) return false;
        if (rating == null || !(1 <= rating && rating <= 5)) return false;
        if (reviewTitle == null || reviewBody == null) return false;
        if (reviewTitle.isEmpty() || reviewBody.isEmpty()) return false;

        return true;
    }

    private String[] getTitleAndArtUrl(String contentType, String contentId) throws Exception {
        String title, artUrl;
        switch (contentType) {
            case ContentType.BOOK:
                Volume volume = new BookDetailsServlet().getDetails(contentId);
                title = volume.getVolumeInfo().getTitle();
                artUrl = volume.getVolumeInfo().getImageLinks().getThumbnail();
                break;
            case ContentType.MOVIE:
                Integer intId = parseInt(contentId);
                if (intId == null) {
                    throw new IllegalArgumentException();
                }
                MovieDb movie = new MovieDetailsServlet().getDetails(intId);
                title = movie.getTitle();
                artUrl = movie.getPosterPath();
                break;
            default:
                throw new IllegalArgumentException();
        }
        return new String[]{title, artUrl};
    }

    private ReviewObject createAndSaveReview(UserObject userObject,
                                             String contentType, String contentId,
                                             String reviewTitle, String reviewBody, int rating) throws Exception {
        String[] titleAndArtUrl = getTitleAndArtUrl(contentType, contentId);
        ReviewObject reviewObject = new ReviewObject(userObject,
                contentType, contentId,
                titleAndArtUrl[0], titleAndArtUrl[0],
                reviewTitle, reviewBody, rating);
        ofy().save().entity(reviewObject).now();
        return reviewObject;
    }
}