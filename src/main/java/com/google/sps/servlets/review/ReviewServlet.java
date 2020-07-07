package com.google.sps.servlets.review;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Review;
import com.google.api.services.books.model.Volume;
import com.google.gson.Gson;
import com.google.sps.KeyConfig;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.google.sps.model.queue.WantToWatchQueueObject;
import com.google.sps.model.review.ReviewObject;
import com.google.sps.servlets.user.UserObject;

import static com.googlecode.objectify.ObjectifyService.ofy;

@WebServlet("/reviews")
public class ReviewServlet extends HttpServlet {

    private final Gson gson = new Gson();

    // Returns reviews by user with ID == userId
    // Returns null if there is no such user or if strings are invalid
    public List<ReviewObject> getUserReviews(String userId) {
        if (userId == null || userId.equals("")) return null;
        if (ofy().load().type(UserObject.class).id(userId).now() == null) return null;

        // TODO: Unify capitalization of userId with EntityListServlet
        // UserService uses `userId` as per Google Style Guide definition of CamelCase
        return ofy().load().type(ReviewObject.class)
                .filter("userId", userId)
                .list();
    }

    // Returns reviews for the item of type contentType with ID == contentId
    // Returns null if there is no such item or if strings are invalid
    public List<ReviewObject> getContentReviews(String contentType, String contentId) {
        // TODO: Consider making enum with types then using `|| !enum.contains(contentType)`
        if (contentType == null
            || !(contentType.equals("book") || contentId.equals("movie"))) return null;
        if (contentId == null || contentId.equals("")) return null;

        // TODO: Returning null if item doesn't exist will require DB entries for media items
        return ofy().load().type(ReviewObject.class)
                .filter("contentType", contentType)
                .filter("contentId", contentId)
                .list();
    }

    /**
     * doGet() returns details of the reviews by a given user or of a given media item
     * Expects either ?contentType={book | movie}&contentId={id}  OR ?userId={id}
     * Returns error 400 if the query parameters are not in either of these formats
     * Returns error 400 if a parameter is empty or invalid (e.g. "bok")
     * Returns error 404 if the given user or media item is not found
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
            reviews = getUserReviews(userId);
        }
        else if (userId == null && contentType != null && contentId != null) {
            reviews = getContentReviews(contentType, contentId);
        }
        else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.getWriter().println(gson.toJson(reviews));
    }

    /**
     * doPost() attempts to post a review for a given item from a user
     * Returns error 400 if any parameters are invalid
     * @param request: expects TODO
     * @param response: returns TODO
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    }
}