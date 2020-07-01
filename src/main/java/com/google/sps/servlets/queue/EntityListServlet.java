package com.google.sps.servlets.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.model.queue.EntityDbQueue;
import com.google.sps.model.queue.QueueResponse;
import com.google.sps.model.queue.WantToWatchQueueObject;
import com.google.sps.model.queue.WatchedQueueObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@WebServlet("/list/entity")
public class EntityListServlet extends HttpServlet {

    private final UserService userService = UserServiceFactory.getUserService();
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private final ObjectMapper mapper = new ObjectMapper();

    private final String typeQueue = "queue";
    private final String typeViewed = "viewed";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=utf-8");
        String userID = request.getParameter("userID");
        String queueType = request.getParameter("entityType");

        if(queueType == null) {
            sendInvalidGetResponse(response);
            return;
        }

        if(!isCorrectListType(queueType)) {
            sendInvalidGetResponse(response);
            return;
        }

        List<? extends EntityDbQueue> result = getQueueWithType(queueType, userID);
        response.getWriter().println(gson.toJsonTree(result));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        QueueResponse newResponse = new QueueResponse();
        String body = parseBody(request);
        EntityDbQueue entityDb = null;
        User user = userService.getCurrentUser();
        if(userService.isUserLoggedIn()) {
            String currentUserID = user.getUserId();
            try {
                entityDb = decideDbType(body);
                if(!currentUserID.equals(entityDb.getUserID())) {
                    sendInvalidPostResponse(response, newResponse);
                    return;
                }
            } catch(Exception exception) {
                sendInvalidPostResponse(response, newResponse);
                return;
            }

            // couldn't parse type correctly, will return a failed attempt
            if(entityDb == null) {
                sendInvalidPostResponse(response, newResponse);
                return;
            }

            ofy().save().entity(entityDb).now();

            newResponse.setSuccess(true);
            newResponse.setEntity(entityDb);
            response.getWriter().println(gson.toJsonTree(newResponse));
        } else {
            sendNotLoggedIn(response);
        }
    }

    /**
     * Helper function to parse out body data from requests
     * @param request: the request to parse out from
     * @return: Stringified JSON request
     * @throws IOException: if the body can't be parsed
     */
    private String parseBody(HttpServletRequest request) throws IOException {
        return request.getReader().lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);
    }

    /**
     * A helper function to get entity data from database
     * @param type: what list type should it look for (queue / watched)
     * @param userID: userID to look for
     * @return: a list of abstracted type EntityDbQueue
     */
    private List<? extends EntityDbQueue> getQueueWithType(String type, String userID) {
        if(type.equals(typeQueue)) {
            return ofy().load().type(WantToWatchQueueObject.class).filter("userID", userID).list();
        } else {
            return ofy().load().type(WatchedQueueObject.class).filter("userID", userID).list();
        }
    }

    /**
     * helper function to check if the type is valid
     * @param queueType: type to check
     * @return: true / false if the type is valid
     */
    private boolean isCorrectListType(String queueType) {
        return queueType.equals(typeQueue) || queueType.equals(typeViewed);
    }

    /**
     * Used to decide which data type we should map POST request to
     * @param body: body to parse into object
     * @return: null if invalid entity type, an EntityDbQueue type object if it can
     * @throws JsonProcessingException: if it can't parse out data.
     */
    private EntityDbQueue decideDbType(String body) throws JsonProcessingException {
        EntityDbQueue entity = mapper.readValue(body, EntityDbQueue.class);
        if(entity.getEntityType().equals(typeViewed)) {
            return mapper.readValue(body, WatchedQueueObject.class);
        } else if(entity.getEntityType().equals(typeQueue)) {
            return mapper.readValue(body, WantToWatchQueueObject.class);
        } else {
            return null;
        }
    }

    /**
     * Sets up return body and response status when an invalid input comes in on POST
     * requests
     * @param httpResponse: response to add status to
     * @param newResponse: response body
     * @throws IOException: if writer can't write (worse case scenario)
     */
    private void sendInvalidPostResponse(HttpServletResponse httpResponse, QueueResponse newResponse) throws IOException {
        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        newResponse.setSuccess(false);
        newResponse.setEntity(null);
        httpResponse.getWriter().println(gson.toJsonTree(newResponse));
    }

    /**
     * Sets status to 400 if we can't parse input
     * @param response: response to add status to
     */
    private void sendInvalidGetResponse(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Sets status to 401 if not logged in
     * @param response: response to add status to
     */
    private void sendNotLoggedIn(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}